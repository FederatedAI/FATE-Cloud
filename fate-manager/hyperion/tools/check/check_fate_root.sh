if [ -d "/data/projects/fate" ]
then
  cd /data/projects/fate
  num=$( ls |wc -l )
  if [ $num -gt 1 ]
  then
    echo "FATE ROOT CHECK: warning: please rename /data/projects/fate"
  else
    echo "FATE ROOT CHECK: success"
  fi
fi