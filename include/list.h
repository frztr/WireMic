#ifndef LIST_H
#define LIST_H
typedef struct list list_t;

list_t* list_new();
int list_get_size(list_t* list);
void list_add(list_t* list, void* value);
void* list_get(list_t* list, int index);
void list_remove(list_t* list, void* value);
#endif