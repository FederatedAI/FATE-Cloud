if [ $maxuser -lt 65535 ];then
  echo "MAX USER PROCESSES CHECK: warning: now max user processes is $maxuser, and need to turn up to 65535"
else
  echo "MAX USER PROCESSES CHECK: success"
fi