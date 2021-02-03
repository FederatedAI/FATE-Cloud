open_ports=( $( /usr/sbin/ss -lnt|grep 'LISTEN'|grep -v ':::' | awk '{print ; }'|cut -d : -f 2|sort -u ) )
tports=()
for oport in ${open_ports[*]};
do
  for port in "9370" "4670" "4671" "9360" "9380" "3306";
  do
    if [ $oport == $port ]
    then
      tports=( ${tports[*]} $port )
    fi
  done
done
if [ ${#tports[*]} -gt 0 ]
then
  echo "PORT CHECK: warning: these ports ${tports[*]} have been used"
else
  echo "PORT CHECK: success"
fi