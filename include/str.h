#ifndef STR_H
#define STR_H
int str_find(char* str, char* input_str, int offset);
char* str_substr(char* str,int start,int len);
char* str_replace(char* str, char* repl_str,  char* to_str);
#endif