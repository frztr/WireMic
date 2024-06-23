#include <gtk-4.0/gtk/gtk.h>
#include <string.h>
#include "./include/gui.h"
#include "./include/modules.h"
#include "./include/store.h"
#include "./include/api.h"
static void turn_on_btn_clicked(GtkWidget *widget, gpointer data)
{
    store_t* store = store_get_instance();
    if(store_get_active(store))
    {
      store_set_active(store,0);
    }
    else
    {
      store_set_active(store,1);
    } 
}

GtkWidget *button;

static void on_active_change(int active)
{
    if(active)
    {
      gtk_button_set_label(GTK_BUTTON(button),"Turn off");
    }
    else
    {
      gtk_button_set_label(GTK_BUTTON(button),"Turn on");
    }
}


static void activate(GtkApplication *app, gpointer user_data)
{
  GtkWidget *window;
  GtkWidget *grid;
  GtkWidget *label;
  
  store_t* store = store_get_instance();
  store_add_active_listener(store,on_active_change);

  window = gtk_application_window_new (app);
  gtk_window_set_title (GTK_WINDOW (window), "WireMicGUI");

  grid = gtk_grid_new ();
  gtk_window_set_child (GTK_WINDOW (window), grid);
  
  char* msg = calloc(512,sizeof(char));
  strcat(msg,user_data);
  label = gtk_label_new(msg);
  gtk_widget_set_margin_top(label,12);
  gtk_widget_set_margin_bottom(label,12);
  gtk_widget_set_margin_start(label,12);
  gtk_widget_set_margin_end(label,12);
  gtk_grid_attach(GTK_GRID(grid),label,0,0,1,1);

  button = gtk_button_new_with_label ("Turn on");
  g_signal_connect (button, "clicked", G_CALLBACK (turn_on_btn_clicked), NULL);
  gtk_grid_attach (GTK_GRID (grid), button, 0, 1, 1, 1);

  gtk_window_present (GTK_WINDOW (window));
}

int build_app(int argc, char *argv[])
{
  GtkApplication *app;
  int status;

  app = gtk_application_new ("org.wiremic.example", G_APPLICATION_FLAGS_NONE);

  char* ip;
  api_t* api = api_get_instance();
  sleep(1);
  ip = api_get_output(api);

  g_signal_connect (app, "activate", G_CALLBACK (activate), ip);
  status = g_application_run (G_APPLICATION (app), argc, argv);
  g_object_unref (app);
  return status;
}