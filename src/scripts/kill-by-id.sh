#! /bin/sh
echo 'Try to kill task: '$1
echo 'Trying to kill following pids: '
ps ux | grep mutation.file=/scratch/schuler/mutation-test-config/result/mutation-task-$1 | awk {'print $2'} 
ps ux | grep mutation.file=/scratch/schuler/mutation-test-config/result/mutation-task-$1 | awk {'print $2'} | xargs kill -9
echo 'pids still alive: '
ps ux | grep mutation.file=/scratch/schuler/mutation-test-config/result/mutation-task-$1 | awk {'print $2'} 
