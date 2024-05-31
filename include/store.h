#ifndef STORE_H
#define STORE_H
typedef struct store store_t;
store_t* store_get_instance();
int store_get_active(store_t* store);
void store_set_active(store_t* store, int active);
typedef void(on_active_change_handler)(int active);
void store_add_active_listener(store_t* store, on_active_change_handler handler);
#endif