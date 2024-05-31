#include "./include/gui.h"
#include "./include/list.h"
#include "./include/store.h"
#include "./include/api.h"
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/prctl.h>
#include <signal.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <sys/wait.h>

void print_active(int active)
{
    printf("Active: %d\n",active);
}

char msg[256];

int main(int argc, char *argv[])
{       
  int inpipefd[2];
  int outpipefd[2];
  char buf[256];
  
  int status;

  pipe(inpipefd);
  pipe(outpipefd);
  pid_t pid = fork();
  if (pid == 0)
  {
    dup2(outpipefd[0], STDIN_FILENO);
    dup2(inpipefd[1], STDOUT_FILENO);
    dup2(inpipefd[1], STDERR_FILENO);
    prctl(PR_SET_PDEATHSIG, SIGTERM);
    close(outpipefd[1]);
    close(inpipefd[0]);
    execlp("java", "java", "-jar", "MicServer.jar", NULL);
    exit(1);
  }
  close(outpipefd[0]);
  close(inpipefd[1]);
  

  api_set_instance(inpipefd[0],outpipefd[1]);

  store_t* store = store_get_instance();
  store_add_active_listener(store,print_active);
  return build_app(argc,argv);

  // kill(pid, SIGKILL);
  // waitpid(pid, &status, 0);
  // return 0;
}

