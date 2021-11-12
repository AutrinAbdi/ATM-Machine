import java.util.*;

//Class meant for the user to interact with, guides user during the use of the ATM Machine
class ATM_Project
{
    public static void main(String[] args)
    {
        //Used to fill ATM Machine with initial amount of cash
        ATM_Machine machine1 = new ATM_Machine(100000);
        System.out.println("Welcome to ATM Machine! Please choose an option to get started!");

        //outer screen (Before Logging in)
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

                //Checks to see if username is stored within database
                if(machine1.user_map.containsKey(user_name))
                {
                    //Prompts user to enter password
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
                System.out.println("Options - Request Payment, Transfer Funds, Deposit, Withdraw, Display Balance, Logout");
                String inner_option = in.nextLine();

                //Uses switch statement to see what user has entered into console
                switch(inner_option)
                {
                    //Messages displayed, asking user for information about the payment being requested
                    case "Request Payment":
                    {
                        //Target username is account that user wants to request funds from
                        System.out.println("Enter target username: ");
                        String target = in.nextLine();
                        //Request amount is amount that user wants to request from target username
                        System.out.println("Enter request amount: ");
                        double amt = in.nextDouble();
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

                        //Allows user to choose between whether they want to pay the requested funds or not
                        System.out.println("Enter the request numbers you want to pay in space-separated order (type exit if you don't want to pay): ");
                        String transfer_inputs = in.nextLine();
                        String[] inputs = transfer_inputs.split(" ");
                        if(inputs[0].toLowerCase().contains("exit"))
                        {
                            break;
                        }

                        ArrayList<Integer> choices = new ArrayList<Integer>();
                        //Checks if the current account has enough of a balance in order to pay for the funds requested (If they accept the transaction)
                        for(String input: inputs)
                        {
                            if(machine1.user_map.get(current_user).balance >= amounts.get(Integer.parseInt(input) - 1))
                            {
                                choices.add(Integer.parseInt(input) - 1);
                            }
                            else
                            {
                                System.out.println("Insufficient Funds to make Payment");
                            }
                        }

                        //Used to transfer the funds from one account to the other depending on the choice entered
                        for(int i: choices)
                        {
                            machine1.transfer_funds(current_user, reqs.get(i).user_id, amounts.get(i));
                            machine1.user_map.get(current_user).received_requests.get(i).user_id = "*";
                        }

                        ArrayList<Request> newList = new ArrayList<Request>();
                        //Adds requested payments to the list that is stored within each account
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
                        double amount = in.nextDouble();
                        machine1.deposit(current_user, amount);
                        break;
                    }

                    //Prompts user to enter amount that they want to withdraw from their account
                    case "Withdraw":
                    {
                        System.out.println("Enter amount to withdraw: ");
                        double amount = in.nextDouble();
                        machine1.withdraw(current_user, amount);
                        break;
                    }

                    //Prompts the ATM Machine to display the current balance of the user that is logged in
                    case "Display Balance":
                    {
                        machine1.print_balance(current_user);
                        break;
                    }

                    //Logs the user out of the inner screen (returns to the outer screen)
                    case "Logout":
                    {
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
        System.out.println("Enter username: ");
        String name = in.nextLine();
        System.out.println("Enter a password:");
        String pass = in.nextLine();
        System.out.println("Add initial deposit (yes/no)?");
        String initial_option = in.nextLine();
        User myUser;
        if(!initial_option.toLowerCase().contains("yes"))
        {
            myUser = new User(name, pass);
        }
        else
        {
            System.out.println("Enter initial deposit: ");
            double initial = in.nextDouble();
            myUser = new User(name, pass, initial);
        }
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
        System.out.println("Successfully Transferred Payment!");
    }

    //print balance of current user
    public void print_balance(String user_number)
    {
        System.out.println(user_map.get(user_number).balance);
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

}

//User class (Holds all information regarding users)
class User
{
    //each user has username, pass, balance
    String username;
    String password;
    double balance;
    // ArrayList<Request> sent_requests = new ArrayList<Request>();
    ArrayList<Request> received_requests = new ArrayList<Request>();

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