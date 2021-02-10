#check mysql
if [ -f /etc/my.cnf ]
then
  echo "MYSQL CHECK: warning: if reinstall mysql, please stop mysql, and rename /etc/my.cnf"
else
  echo "MYSQL CHECK: success"
fi
