#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "./include/api.h"

typedef struct api 
{
    int inpipefd;
    int outpipefd;
    char buf[512];
} api_t;

static api_t* api_instance;

api_t* api_new(int inpipefd, int outpipefd)
{
    api_t* instance = malloc(sizeof(api_t));
    instance->inpipefd = inpipefd;
    instance->outpipefd = outpipefd;
    return instance;
}

void api_set_instance(int inpipefd, int outpipefd)
{
    api_instance = api_new(inpipefd,outpipefd);
}

api_t* api_get_instance()
{
    return api_instance;
}

char* api_get_output(api_t* api)
{
    read(api->inpipefd, api->buf, 256);
    // printf("%s", api->buf);
    char* str = malloc(strlen(api->buf)*sizeof(char));
    strcpy(str,api->buf);
    memset(&(api->buf[0]),0,strlen(api->buf)); 
    return str;
}

void api_write_input(api_t* api,char* out)
{
    write(api->outpipefd, out, strlen(out));
}