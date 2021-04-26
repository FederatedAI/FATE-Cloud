mem=$( free -g | grep 'Mem:'|awk '{ print $2; }' )
swap=$( free -g | grep 'Swap' |awk '{ print $2; }' )
if [ $swap -lt $(( 128-$mem )) ];then
  echo "SWAP CHECK: warning: now swap is $swap, need to turn up"
else
  echo "SWAP CHECK: success"
fi