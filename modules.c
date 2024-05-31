#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "./include/str.h"
#include "./include/modules.h"

char* get_ip()
{
    
    char* text = get_file_output("/sbin/ifconfig wlp3s0");
    int inet = str_find(text,"inet ",0) + 5;
    int inet_end = str_find(text," ",inet+1);
    char* ip = str_substr(text,inet,inet_end-inet);
    return ip;
}

char* get_file_output(char* file_path)
{
    FILE *fp;
    
    char returnData[64];

    int i = 0;
    fp = popen(file_path, "r");  
    while (fgets(returnData, 64, fp) != NULL)
    {
        i++;
    }
    pclose(fp);

    char* text = calloc(64*(i+1),sizeof(char));
    fp = popen(file_path, "r");  
    while (fgets(returnData, 64, fp) != NULL)
    {
        strcat(text,returnData);
    }
    pclose(fp);

    return text;
}