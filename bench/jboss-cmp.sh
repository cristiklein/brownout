#!/bin/tcsh

setenv EJBDIR /users/cecchet/RUBiS/EJB_EntityBean_id

# Go back to RUBiS root directory
cd ..

# Browse only JBoss

cp ./workload/browse_only_transitions_7.txt ./workload/transitions.txt

foreach i ( rubis.properties_20 rubis.properties_40 rubis.properties_60 rubis.properties_80 rubis.properties_100 rubis.properties_120 rubis.properties_140 rubis.properties_160 rubis.properties_180 rubis.properties_200 rubis.properties_220 rubis.properties_240 rubis.properties_260 rubis.properties_280 rubis.properties_300)
  cp bench/$i Client/rubis.properties
  rsh sci21 -n -l root ${EJBDIR}/tomcat_stop.sh
  rsh sci20 ${EJBDIR}/jboss_stop.sh
  sleep 4
  rsh sci22 ${EJBDIR}/update_ids.sh
  rsh sci21 -n -l root ${EJBDIR}/tomcat_jboss_start.sh &
  rsh sci20 "cd ${EJBDIR} ; jboss_start.sh" &
  sleep 10
  bench/flush_cache 490000
  rsh sci8 RUBiS/bench/flush_cache 490000	# remote client
  rsh sci20 RUBiS/bench/flush_cache 880000      # ejb server
  rsh sci21 RUBiS/bench/flush_cache 880000 	# servlet server
  rsh sci23 RUBiS/bench/flush_cache 880000 	# web server
  rsh sci22 RUBiS/bench/flush_cache 880000	# database
  make emulator
end

# Default JBoss

cp ./workload/default_transitions_7.txt ./workload/transitions.txt


foreach i ( rubis.properties_20 rubis.properties_40 rubis.properties_60 rubis.properties_80 rubis.properties_100 rubis.properties_120 rubis.properties_140 rubis.properties_160 rubis.properties_180 rubis.properties_200 rubis.properties_220 rubis.properties_240 rubis.properties_260 rubis.properties_280 rubis.properties_300)
  cp bench/$i Client/rubis.properties
  cp bench/$i Client/rubis.properties
  rsh sci21 -n -l root ${EJBDIR}/tomcat_stop.sh
  rsh sci20 ${EJBDIR}/jboss_stop.sh
  sleep 4
  rsh sci22 ${EJBDIR}/update_ids.sh
  rsh sci21 -n -l root ${EJBDIR}/tomcat_jboss_start.sh &
  rsh sci20 "cd ${EJBDIR} ; jboss_start.sh" &
  sleep 10
  bench/flush_cache 490000
  rsh sci8 RUBiS/bench/flush_cache 490000	# remote client
  rsh sci20 RUBiS/bench/flush_cache 880000      # ejb server
  rsh sci21 RUBiS/bench/flush_cache 880000 	# servlet server
  rsh sci23 RUBiS/bench/flush_cache 880000 	# web server
  rsh sci22 RUBiS/bench/flush_cache 880000	# database
  make emulator
end

rsh sci20 ${EJBDIR}/jonas_stop.sh
sleep 4
