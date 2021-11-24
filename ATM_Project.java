import java.util.*;

//Class meant for the user to interact with, guides user during the use of the ATM Machine
class ATM_Project
{
    public static void main(String[] args)
    {
        //Used to fill ATM Machine with initial amount of cash
        ATM_Machine machine1 = new ATM_Machine(100000);
        System.out.println("Welcome to ATM Machine! Please choose an option to get started!");

        //outer screen (Before Logging In)
        //If ATM machine is still being used (User has not entered "Exit")
        while(true)
        {
            String current_user = null;
            Scanner in = new Scanner(System.in);

            //Provides options for user at the start of ATM Machine
            System.out.println("Options - Login, Create Account, Exit");
            String option = in.nextLine();
            boolean flag = false;

            //Exits the ATM Machine
            if(option.equals("Exit"))
            {
                break;
            }

            //Allows user to login into a created account
            if(option.equals("Login"))
            {
                //Prompts user to enter username
                System.out.println("Enter username: ");
                String user_name = in.nextLine();

                //Checks if username is contained in database
                if(machine1.user_map.containsKey(user_name))
                {
                    System.out.println("Enter Password: ");
                    String pw = in.nextLine();

                    //Checks to see if username entered (key) matches password (value)
                    if(machine1.user_map.get(user_name).password.equals(pw))
                    {
                        //Once entered correctly, provides a welcome message to the user
                        System.out.println("Login Successful!");
                        System.out.println("Welcome: " + machine1.user_map.get(user_name).username);
                        current_user = user_name;
                    }
                    else
                    {
                        //If incorrect password is entered, displays this message
                        System.out.println("Invalid password");
                        continue;
                    }
                }
                else
                {
                    //If incorrect username is entered, displays this message
                    System.out.println("Invalid username");
                    continue;
                }
            }
            //If user types "Create Account"
            else if(option.equals("Create Account"))
            {
                machine1.create_account();
                continue;
            }
            //If user types anything besides the given options
            else
            {
                System.out.println("Invalid");
                continue;
            }

            //inner screen (After logging in)
            while(true)
            {
                //Provides options for user after logging into the ATM Machine
                System.out.println("Options - Request Payment, Transfer Funds, Deposit, Withdraw, Display Balance, View Payment Notifications, Change Password, Logout");
                String inner_option = in.nextLine();

                //Uses switch statement to see what user has entered into console
                switch(inner_option)
                {
                    //Messages displayed, asking user for information about the payment being requested
                    case "Request Payment":
                    {
                        //System.out.println(machine1.list_of_users.size());
                        if(machine1.list_of_users.size() == 1)
                        {
                            System.out.println("No other users in the system to request payment from.");
                            break;
                        }

                        //Display all the users, except the current user
                        System.out.println("Choose to request money from the following users:");
                        for(String key: machine1.user_map.keySet())
                        {
                            if(!current_user.equals(key))
                            {
                                System.out.println(key);
                            }
                        }

                        //Target username is account that user wants to request funds from
                        System.out.println("Enter target username: ");

                        boolean continue1 = false;
                        String target;

                        while(true)
                        {
                            target = in.nextLine();
                            if(!machine1.user_map.containsKey(target))
                            {
                                //Checks if username being targeted is already there
                                continue1 = true;
                                System.out.println("Target username not found.");
                                break;
                            }
                            else if(target.equals(current_user))
                            {
                                //Checks if user types in their own username
                                continue1 = true;
                                System.out.println("Cannot request payment from an account in use.");
                                break;
                            }
                            else
                            {
                                break;
                            }
                        }
                        if(continue1)
                        {
                            continue;
                        }

                        //Request amount is amount that user wants to request from target username
                        System.out.println("Enter request amount: ");
                        double amt;
                        String double_string = "";
                        while(true)
                        {

                            double_string = in.nextLine();
                            try
                            {
                                amt = Double.parseDouble(double_string);
                                if(amt < 0)
                                {
                                    // In the case of an invalid input
                                    System.out.println("Invalid input, please enter a valid request amount: ");
                                    continue;
                                }
                                break;
                            }
                            catch(Exception e)
                            {
                                // In the case of an invalid input
                                System.out.println("Invalid input, please enter a valid request amount: ");
                            }
                        }

                        machine1.request_payment(current_user, target, amt);
                        break;
                    }

                    //Messages displayed, asking user for information about the funds being transferred
                    case "Transfer Funds":
                    {
                        ArrayList<Request> reqs = new ArrayList<Request>();
                        ArrayList<Double> amounts = new ArrayList<Double>();

                        // Displays any requests from other accounts on current account
                        System.out.println("Displaying your current requests: ");
                        int count = 1;
                        for(Request r: machine1.user_map.get(current_user).received_requests)
                        {
                            System.out.println("Request " + count + " of " + r.amt + " from " + r.user_id);
                            amounts.add(r.amt);
                            reqs.add(r);
                            count++;
                        }
                        if(count == 1)
                        {
                            System.out.println("You have no current payment requests.");
                            break;
                        }

                        //Allows user to choose between whether they want to pay the requested funds or not
                        System.out.println("Enter the request numbers you want to pay in space-separated order (type exit if you don't want to pay or delete to remove requests): ");
                        String transfer_inputs = in.nextLine();
                        String[] inputs = transfer_inputs.split(" ");
                        if(inputs[0].toLowerCase().contains("exit"))
                        {
                            break;
                        }
                        if(inputs[0].toLowerCase().contains("delete"))
                        {
                            //Prompts user to select which requests they want to remove
                            System.out.println("Enter the request numbers you want to REMOVE in space-separated order (type exit to cancel): ");
                            String delete_inputs = in.nextLine();
                            String[] delete_array = delete_inputs.split(" ");
                            boolean break2 = false;
                            for(String delete_element: delete_array)
                            {
                                try
                                {
                                    if(Integer.parseInt(delete_element) - 1 < 0 || Integer.parseInt(delete_element) - 1 >= reqs.size())
                                    {
                                        //In the case of an invalid input
                                        System.out.println("Input is invalid. Try to remove requests again.");
                                        break2 = true;
                                        break;
                                    }
                                }
                                catch(Exception e)
                                {
                                    //In the case of an invalid input
                                    System.out.println("Input is invalid. Try to remove requests again.");
                                    break2 = true;
                                    break;
                                }
                            }
                            if(break2)
                            {
                                break;
                            }

                            for(String delete_element: delete_array)
                            {
                                int current_delete = Integer.parseInt(delete_element) - 1;
                                machine1.user_map.get(current_user).received_requests.get(current_delete).user_id = "*";
                            }

                            ArrayList<Request> newList = new ArrayList<Request>();
                            for(Request req: machine1.user_map.get(current_user).received_requests)
                            {
                                if(!req.user_id.equals("*"))
                                {
                                    newList.add(req);
                                }
                            }

                            machine1.user_map.get(current_user).received_requests = newList;

                            break;
                        }

                        boolean break_1 = false;
                        ArrayList<Integer> choices = new ArrayList<Integer>();

                        //Checks if the current account has enough of a balance in order to pay for the funds requested (If they accept the transaction)
                        for(String input: inputs)
                        {
                            try
                            {
                                if(Integer.parseInt(input) - 1 < 0 || Integer.parseInt(input) - 1 >= reqs.size())
                                {
                                    System.out.println("Input is invalid. Try to transfer funds again.");
                                    break_1 = true;
                                    break;
                                }
                                if(machine1.user_map.get(current_user).balance >= amounts.get(Integer.parseInt(input) - 1))
                                {
                                    choices.add(Integer.parseInt(input) - 1);
                                }
                                else
                                {
                                    System.out.println("Insufficient Funds to make Payment");
                                    break_1 = true;
                                    break;
                                }
                            }
                            catch(Exception e)
                            {
                                System.out.println("Input is invalid. Try to transfer funds again.");
                            }

                        }

                        if(break_1)
                        {
                            break;
                        }

                        //Used to transfer the funds from one account to the other depending on the choice entered
                        for(int i: choices)
                        {
                            machine1.transfer_funds(current_user, reqs.get(i).user_id, amounts.get(i));
                            machine1.user_map.get(current_user).received_requests.get(i).user_id = "*";
                        }

                        //Adds requested payments to the list that is stored within each account
                        ArrayList<Request> newList = new ArrayList<Request>();
                        for(Request req: machine1.user_map.get(current_user).received_requests)
                        {
                            if(!req.user_id.equals("*"))
                            {
                                newList.add(req);
                            }
                        }

                        machine1.user_map.get(current_user).received_requests = newList;

                        break;
                    }

                    //Prompts user to enter amount that they want to deposit within their account
                    case "Deposit":
                    {
                        System.out.println("Enter amount to deposit: ");
                        //
                        String double_string = "";
                        double amount;
                        while(true)
                        {

                            double_string = in.nextLine();
                            try
                            {
                                amount = Double.parseDouble(double_string);
                                if(amount < 0)
                                {
                                    System.out.println("Invalid input, please enter a valid deposit amount: ");
                                    continue;
                                }
                                break;
                            }
                            catch(Exception e)
                            {
                                System.out.println("Invalid input, please enter a valid deposit amount: ");
                            }
                        }
                        //
                        machine1.deposit(current_user, amount);
                        break;
                    }
                    //Prompts user to enter amount that they want to withdraw from their account
                    case "Withdraw":
                    {
                        System.out.println("Enter amount to withdraw: ");
                        double amount;
                        String double_string = "";
                        while(true)
                        {

                            double_string = in.nextLine();
                            try
                            {
                                amount = Double.parseDouble(double_string);
                                if(amount < 0)
                                {
                                    System.out.println("Invalid input, please enter a valid withdraw amount: ");
                                    continue;
                                }
                                break;
                            }
                            catch(Exception e)
                            {
                                System.out.println("Invalid input, please enter a valid withdraw amount: ");
                            }
                        }
                        machine1.withdraw(current_user, amount);
                        break;
                    }
                    //Prompts the ATM Machine to display the current balance of the user that is logged in
                    case "Display Balance":
                    {
                        machine1.print_balance(current_user);
                        break;
                    }
                    //Prompts the ATM Machine to display any payment notifications
                    case "View Payment Notifications":
                    {
                        machine1.print_notifications(current_user);
                        break;
                    }
                    //Prompts the user to enter a new password
                    case "Change Password":
                    {
                        System.out.println("New password must be at least 4 characters long and cannot have leading or trailing spaces. Strong password must be 8 characters long, must have 1 lower case letter, 1 upper case letter, 1 digit, and 1 special character. Only one of the Strong password requirements can be missed");
                        System.out.println("Enter a new password:");
                        String pass;
                        while(true)
                        {
                            pass = in.nextLine();
                            if(machine1.user_map.get(current_user).password.equals(pass))
                            {
                                System.out.println("You have entered your current password.");
                                break;
                            }
                            if(machine1.passwordChecker(pass) > 1)
                            {
                                continue;
                            }
                            else
                            {
                                machine1.user_map.get(current_user).password = pass;
                                break;
                            }
                        }
                        break;
                    }
                    //Logs the user out of the inner screen (returns to the outer screen)
                    case "Logout":
                    {
                        machine1.user_map.get(current_user).mailbox = new ArrayList<String>();
                        flag = true;
                        break;
                    }
                    //Results in this case if an invalid input was entered within the inner screen
                    default:
                    {
                        System.out.println("Invalid Input");
                        break;
                    }
                }

                //Allows user to exit the ATM Machine completely
                if(flag)
                {
                    flag = !flag;
                    break;
                }

            }
        }

    }
}

//Class is used to hold all methods regarding user action within the ATM
class ATM_Machine
{
    //instance variables
    double stock;
    ArrayList<User> list_of_users;
    HashMap<String, User> user_map = new HashMap<String, User>();

    //atm machine constructor
    public ATM_Machine(double stock)
    {
        this.stock = stock;
        this.list_of_users = new ArrayList<User>();
    }

    //create new account
    public void create_account()
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a username with at least 4 characters and no leading/trailing spaces");
        System.out.println("Enter username: ");
        String name;
        while(true)
        {
            //checks for login credential validity
            name = in.nextLine();
            int length = name.length();
            if(user_map.containsKey(name))
            {
                System.out.println("Username already taken. Please enter another username: ");
            }
            else if(name.length() == 0)
            {
                System.out.println("No characters inputted. Please enter a valid username: ");
            }
            else if(name.trim().length() < length)
            {
                System.out.println("Leading or trailing spaces are not allowed. Please enter username again: ");
            }
            else if(name.length() < 4)
            {
                System.out.println("Username must be at least 4 characters in length. Please enter username again: ");
            }
            else
            {
                break;
            }
        }

        //checks for strong password
        System.out.println("Password must be at least 4 characters long and cannot have leading or trailing spaces. Strong password must be 8 characters long, must have 1 lower case letter, 1 upper case letter, 1 digit, and 1 special character. Only one of the Strong password requirements can be missed");
        System.out.println("Enter a password:");
        String pass;
        while(true)
        {
            pass = in.nextLine();
            if(passwordChecker(pass) > 1)
            {
                continue;
            }
            else
            {
                break;
            }
        }

        //allows user to enter initial deposit when creating an account
        System.out.println("Add initial deposit (yes/no)?");
        User myUser;
        while(true)
        {
            String initial_option = in.nextLine();
            if(initial_option.toLowerCase().contains("no"))
            {
                myUser = new User(name, pass);
                break;
            }
            else if(initial_option.toLowerCase().contains("yes"))
            {
                System.out.println("Enter initial deposit: ");
                String initial_string = "";
                double initial;
                while(true)
                {

                    initial_string = in.nextLine();
                    try
                    {
                        initial = Double.parseDouble(initial_string);
                        if(initial < 0)
                        {
                            //in the case of an invalid input
                            System.out.println("Invalid input, please enter a valid initial deposit: ");
                            continue;
                        }
                        break;
                    }
                    catch(Exception e)
                    {
                        //in the case of an invalid input
                        System.out.println("Invalid input, please enter a valid initial deposit: ");
                    }
                }

                myUser = new User(name, pass, initial);
                break;
            }
            else
            {
                //in the case of an invalid input
                System.out.println("Invalid input, please enter yes/no for initial deposit option: ");
            }
        }

        list_of_users.add(myUser);
        user_map.put(name, myUser);
    }

    //request a payment from another user
    public void request_payment(String requester_id, String potential_sender, double amount)
    {
        //user_map.get(requester_id).sent_requests.add(new Request(potential_sender, amount));
        user_map.get(potential_sender).received_requests.add(new Request(requester_id, amount));
        System.out.println("Successfully Requested Payment of " + amount + " from: " + potential_sender);
    }

    //allows users to transfer funds from one account to another
    public void transfer_funds(String sender_id, String receiver_id, double amount)
    {
        user_map.get(sender_id).balance -= amount;
        user_map.get(receiver_id).balance += amount;
        String notification = "Received " + amount + " from " + sender_id + " .";
        user_map.get(receiver_id).mailbox.add(notification);
        System.out.println("Successfully Transferred Payment!");
    }

    //print balance of current user
    public void print_balance(String user_number)
    {
        System.out.println(user_map.get(user_number).balance);
    }

    //prints out the notifications through the checking of a database
    public void print_notifications(String user_number)
    {
        ArrayList<String> notifications = user_map.get(user_number).mailbox;
        if(notifications.isEmpty())
        {
            System.out.println("No new payment notifications.");
            return;
        }
        System.out.println("Below are new payments received after your last login/session: ");
        for(String n: notifications)
        {
            System.out.println(n);
        }
    }

    //allows user to reset their password
    public void reset_user_password(int user_number)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter new password: ");
        String new_pass = in.nextLine();
        if(new_pass.equals(user_map.get(user_number).password))
        {
            System.out.println("This is your current password.");
            return;
        }
        user_map.get(user_number).reset_password(new_pass);
        System.out.println("Successfully changed password!");
    }

    //allows user to deposit a designated amount of money
    public void deposit(String user_number, double amount)
    {
        stock += amount;
        user_map.get(user_number).balance += amount;
        System.out.println("Successful Deposit of: " + amount);
    }

    //allows user to withdraw a designated amount of money
    public void withdraw(String user_number, double amount)
    {
        if(amount > user_map.get(user_number).balance)
        {
            System.out.println("Not enough in user balance!");
            return;
        }
        else if(stock - amount < 0)
        {
            System.out.println("Not enough cash in stock!");
            return;
        }
        stock -= amount;
        user_map.get(user_number).balance -= amount;
        System.out.println("Successful Withdrawl of: " + amount);
    }

    //allows system to check for user-input of password in order to see if it is a strong password
    //provides details about what a strong password is
    public int passwordChecker(String pass)
    {
        boolean lengthof8 = false;
        boolean containslowerletter = false;
        boolean containsupperletter = false;
        boolean containsdigit = false;
        boolean containsspecialcharacter = false;

        if(pass.length() < 4)
        {
            System.out.println("Password must be at least 4 characters long. Try again.");
            System.out.println("Enter Password: ");
            return 10;
        }
        else if(pass.length() >= 8)
        {
            lengthof8 = true;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < pass.length(); i++)
        {
            sb.append(pass.charAt(i));
        }
        String copy = String.valueOf(sb);
        if(!copy.trim().equals(pass))
        {
            System.out.println("Passwords with leading or trailing spaces are not accepted. Try again.");
            System.out.println("Enter Password: ");
            return 10;
        }


        for(int i = 0; i < pass.length(); i++)
        {
            if(Character.isDigit(pass.charAt(i)))
            {
                //System.out.println("dig");
                containsdigit = true;
            }
            else if(Character.isLowerCase(pass.charAt(i)))
            {
                //System.out.println("lower");
                containslowerletter = true;
            }
            else if(Character.isUpperCase(pass.charAt(i)))
            {
                //System.out.println("upper");
                containsupperletter = true;
            }
            else
            {
                //System.out.println("special");
                containsspecialcharacter = true;
            }
        }

        String issues = "";
        int count = 0;
        if(lengthof8 && containslowerletter && containsupperletter && containsdigit && containsspecialcharacter)
        {
            System.out.println("Strong password detected.");
        }
        if(!lengthof8)
        {
            issues += "Strong password is at least 8 characters long. ";
            count++;
        }
        if(!containslowerletter)
        {
            issues += "Strong password should have at least 1 lower case letter. ";
            count++;
        }
        if(!containsupperletter)
        {
            issues += "Strong password should have at least 1 upper case letter. ";
            count++;
        }
        if(!containsdigit)
        {
            issues += "Strong password should have at least 1 digit. ";
            count++;
        }
        if(!containsspecialcharacter)
        {
            issues += "Strong password should have at least 1 special character. ";
            count++;
        }

        if(count == 1)
        {
            System.out.println("Password is acceptable, but is not strong. Warning, this issue should be addressed in the future: " + issues);
            return 1;
        }
        else if(count > 1)
        {
            System.out.println("Password is NOT acceptable. You must try again. These issues must be addressed: " + issues);
            System.out.println("Enter Password: ");
            return 2;
        }
        return 0;
    }

}

//User class
class User
{
    //each user has username, pass, balance
    String username;
    String password;
    double balance;
    // ArrayList<Request> sent_requests = new ArrayList<Request>();
    ArrayList<Request> received_requests = new ArrayList<Request>();
    ArrayList<String> mailbox = new ArrayList<String>();

    //constructor without initial balance
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.balance = 0.0;
    }

    //constructor with initial balance
    public User(String username, String password, double initial_balance)
    {
        this.username = username;
        this.password = password;
        this.balance = initial_balance;
    }

    //set method used to change password
    public void reset_password(String new_password)
    {
        password = new_password;
    }
}

//This class is meant to provide information in the case of a user requesting a payment
class Request
{
    //instance variables
    String user_id;
    double amt;

    //constructor with designated user ID and amount being requested
    public Request(String user_id, double amt)
    {
        this.user_id = user_id;
        this.amt = amt;
    }
}