#include <stdlib.h>
#include "./include/list.h"

typedef struct elem elem_t;

typedef struct elem
{
    void* content;
    elem_t* prev;
    elem_t* next;

} elem_t;

typedef struct list
{
    int size;
    elem_t* start_ptr;

} list_t;

static elem_t* elem_new(void* value)
{
    elem_t* new = malloc(sizeof(elem_t));
    new->content = value;
    return new;
}

list_t* list_new()
{
    list_t* l = malloc(sizeof(list_t));
    l->size = 0;
    return l;
}

int list_get_size(list_t* list)
{
    return list->size;
}

void list_add(list_t* list, void* value)
{
    elem_t* new = elem_new(value);

    elem_t* last = list->start_ptr;
    if(last==NULL)
    {
        list->start_ptr = new;
    }
    else
    {
        for(int i = 1;i<list->size;i++)
        {
            last = last->next;
        }
        new->prev = last;
        last->next = new;
    }
    list->size++;
}

void* list_get(list_t* list, int index)
{
    elem_t* elem = list->start_ptr;
    if(elem == NULL)
    {
        //throw exception
    }
    for(int i = 0;i<index;i++)
    {
        elem = elem->next;
    }
    return elem->content;
}

void list_remove(list_t* list,void* value)
{
    elem_t* elem = list->start_ptr;
    if(elem == NULL)
    {
        //throw exception
    }

    for(int i = 0;i<list->size;i++)
    {
        if(elem->content == value)
        {
            elem_t* prev = elem->prev;
            elem_t* next = elem->next;

            if(prev != NULL)
            {
                prev->next = next;
            }
            if(next!= NULL)
            {
                next->prev = prev;
            }

            (list->size)--;
            return;
        }
        elem = elem->next;
    }
}