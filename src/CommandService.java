public class CommandService
{
    private final static CommandService service = new CommandService();
    private CommandService(){}

    public static CommandService getInstance() {
        return service;
    }

    public void ExecuteCommand(String str)
    {
        switch (str) {
            case "Start":
                AppContext.getInstance().setListening(true);
                break;
            case "Stop":
                AppContext.getInstance().setListening(false);
                break;
            case "Close":
                System.out.println("Closing application");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid Command");
        }
    }
}
