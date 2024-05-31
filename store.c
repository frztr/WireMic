#include "./include/store.h"
#include <stdlib.h>
#include "./include/list.h"
#include "./include/modules.h"
#include <stdio.h>
#include <signal.h>
#include "./include/api.h"
#include <unistd.h>

typedef struct store
{
    int active;
    list_t* active_listeners;
} store_t;

typedef void(on_active_change_handler)(int active);

static store_t* instance;

static store_t* store_instance()
{
    store_t* store = malloc(sizeof(store_t));
    store->active_listeners = list_new();
    store->active = 0;
    return store;
}

store_t* store_get_instance()
{
    if(instance == NULL)
    {
        instance = store_instance();
    } 
    return instance;
}

int store_get_active(store_t* store)
{
    return store->active;
}

static void start_mic(store_t* store);
static void stop_mic(store_t* store);

void store_set_active(store_t* store, int active)
{
    if(active != store->active)
    {
        store->active = active;
        for(int i = 0; i < list_get_size(store->active_listeners);i++)
        {
            on_active_change_handler* handler = list_get(store->active_listeners,i);
            handler(active);
        }

        if(active)
        {
            start_mic(store);
        }
        else
        {
            stop_mic(store);
        }
    }
}

void store_add_active_listener(store_t* store, on_active_change_handler handler)
{
    list_add(store->active_listeners,handler);
}

static void start_mic(store_t* store)
{
    api_t* inst = api_get_instance();
    api_write_input(inst,"Start\n");
}

static void stop_mic(store_t* store)
{
    api_t* inst = api_get_instance();
    api_write_input(inst,"Stop\n");
}

