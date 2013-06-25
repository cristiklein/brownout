#!/usr/bin/env python
from __future__ import print_function, division

import datetime
import logging
from math import ceil
from optparse import OptionParser
import os
import select
import socket
from sys import stderr
from time import sleep
import time

# Controller logic
def executeController(pole, setPoint, serviceTime, serviceLevel):
	# special value: no control
	if pole == 0:
		return serviceLevel

	alpha = serviceTime / serviceLevel # very rough estimate
	# NOTE: control knob allowing to smooth service times
	# To enable this, you *must* add a new state variable (alpha) to the controller.
	#alpha = 0.5 * alpha + 0.5 * serviceTime / previousServiceLevel # very rough estimate
	error = setPoint - serviceTime
	# NOTE: control knob allowing slow increase
	if error > 0:
		error *= 0.1
	serviceLevel = serviceLevel + (1 / alpha) * (1 - pole) * error

	# saturation, service level is a probability
	serviceLevel = max(serviceLevel, 0.0)
	serviceLevel = min(serviceLevel, 1.0)
	return serviceLevel
# end controller logic

def now():
	return time.time()

def avg(a):
	if len(a) == 0:
		return float('nan')
	return sum(a) / len(a)

def median(a):
	# assumes a is sorted
	n = len(a)
	if n == 0:
		return float('nan')
	if n % 2 == 0:
		return (a[n//2-1] + a[n//2]) / 2
	else:
		return a[n//2]

def quartiles(a):
	n = len(a)
	if n == 0:
		return [ float('nan') ] * 6
	if n == 1:
		return [ a[0] ] * 6

	a = sorted(a)
	ret = []
	ret.append(a[0])
	ret.append(median(a[:n//2]))
	ret.append(median(a))
	ret.append(median(a[n//2:]))
	ret.append(a[-1])
	ret.append(avg(a))

	return ret

class UnixTimeStampFormatter(logging.Formatter):
	def formatTime(self, record, datefmt = None):
		return "{0:.6f}".format(record.created)

def main():
	# Set up logging
	logChannel = logging.StreamHandler()
	logChannel.setFormatter(UnixTimeStampFormatter("%(asctime)s %(levelname)-5.5s [%(name)s] %(message)s"))
	logging.getLogger().addHandler(logChannel)
	logging.getLogger().setLevel(logging.DEBUG)

	# Parse command-line
	parser = OptionParser()
	parser.add_option("--pole"    , type="float", help="use this pole value (default: %default)", default = 0.9)
	parser.add_option("--setPoint", type="float", help="keep maximum latency around this value (default: %default)", default = 1)
	parser.add_option("--controlInterval", type="float", help="time between control iterations (default: %default)", default = 1)
	parser.add_option("--measureInterval", type="float", help="act based on maximum latency this far in the past (default: %default)", default = 5)
	parser.add_option("--rmIp", type="string", help="send matching values to this IP (default: %default)", default = "192.168.122.1")
	parser.add_option("--rmPort", type="int", help="send matching values to this UDP port (default: %default)", default = 2712)
	(options, args) = parser.parse_args()

	# Setup socket to listen for latency reports
	appSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	appSocket.bind(("localhost", 2712))

	# Setup socket to send matching values
	rmSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

	# Initialize control loop
	poll = select.poll()
	poll.register(appSocket, select.POLLIN)
	lastControl = now()
	lastTotalRequests = 0
	timestampedLatencies = [] # tuples of timestamp, latency
	totalRequests = 0
	serviceLevel = 0.5

	# Control loop
	while True:
		# Wait for next control iteration or message from application
		waitFor = max(ceil((lastControl + options.controlInterval - now()) * 1000), 1)
		events = poll.poll(waitFor)

		_now = now() # i.e., all following operations are "atomic" with respect to time
		# If we received a latency report, record it
		if events:
			data, address = appSocket.recvfrom(4096, socket.MSG_DONTWAIT)
			timestampedLatencies.append((_now, float(data)))
			totalRequests += 1

		# Run control algorithm if it's time for it
		if _now - lastControl >= options.controlInterval:
			# Filter latencies: only take those from the measure interval
			timestampedLatencies = [ (t, l)
				for t, l in timestampedLatencies if t > _now - options.measureInterval ]
			latencies = [ l for t,l in timestampedLatencies ]

			# Do we have new reports?
			if latencies:
				# Execute controller
				serviceLevel = executeController(
					pole = options.pole,
					setPoint = options.setPoint,
					serviceTime = max(latencies),
					serviceLevel = serviceLevel,
				)
				
				# Report performance to RM
				matchingValue = min([ 1 - latency / options.setPoint for latency in latencies ])
				rmSocket.sendto(str(matchingValue), (options.rmIp, options.rmPort))

				# Print statistics
				latencyStat = quartiles(latencies)
				logging.info("latency={0:.0f}:{1:.0f}:{2:.0f}:{3:.0f}:{4:.0f}:({5:.0f})ms throughput={6:.0f}rps rr={7:.2f}% total={8} perf={9:.3f}".format(
					latencyStat[0] * 1000,
					latencyStat[1] * 1000,
					latencyStat[2] * 1000,
					latencyStat[3] * 1000,
					latencyStat[4] * 1000,
					latencyStat[5] * 1000,
					(totalRequests - lastTotalRequests) / (_now-lastControl),
					serviceLevel * 100,
					totalRequests,
					matchingValue,
				))
				with open('/tmp/serviceLevel.tmp', 'w') as f:
					print(serviceLevel, file = f)
				os.rename('/tmp/serviceLevel.tmp', '/tmp/serviceLevel')
			else:
				logging.info("No traffic since last control interval.")
			lastControl = _now
			lastTotalRequests = totalRequests
	s.close()

if __name__ == "__main__":
	main()
