#ifndef API_H
#define API_H
typedef struct api api_t;

void api_set_instance(int inpipefd, int outpipefd);
api_t* api_get_instance();
char* api_get_output(api_t* api);
void api_write_input(api_t* api,char* out);
#endif