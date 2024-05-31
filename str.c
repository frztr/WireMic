#include "./include/str.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
int str_find(char* str, char* input_str,int offset)
{
    int i = offset;
    while(i<=(strlen(str)-strlen(input_str)))
    {
        char* substr = str_substr(str,i,strlen(input_str));
        if(!strcmp(substr,input_str))
        {
            return i;
        }
        i++;
    }
    return -1;
}

char* str_substr(char* str,int start,int len)
{
    char* ret = malloc(sizeof(char)*len);
    int i = 0;
    while(i<len)
    {
        ret[i] = str[i+start];
        i++;
    }
    return ret;
}

char* str_replace(char* str, char* repl_str, char* to_str)
{
    int offset = 0;
    int checked = 0;
    int count = 0;

    while(checked != 1)
    {
        int found = str_find(str,repl_str,offset);
        if(found == -1)
        {
            checked = 1;
        }
        else
        {
            count++;
            offset = found + 1;
        }
    }

    offset = 0;
    char ret[strlen(str)+( strlen(to_str)-strlen(repl_str))*count];
    char* _ret = ret;

    for(int i = 0; i < count; i++)
    {
        int found = str_find(str,repl_str,offset);
        
        strcat(ret,str_substr(str,offset,found - offset));
        strcat(ret,to_str);
        offset = found + strlen(repl_str);
    }

    strcat(ret,str_substr(str,offset,strlen(str) - offset));

    return _ret;
}