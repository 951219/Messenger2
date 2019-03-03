import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.StandardOpenOption.APPEND;


public class MessengerUtilities {


    private Scanner scanner = new Scanner(System.in);
    private Path pathToUsersInfo = Paths.get("usersInfo.txt");
    private Path pathToFileNames = Paths.get("fileNames.txt");
    private Path pathToOnlineUsers = Paths.get("onlineUsers.txt");
    private String loggedInUser = "";


    public void addUser() throws IOException {
        System.out.println("Enter name");

        String name = scanner.nextLine();
        if (isUserExisting(name)) {
            System.out.println("This name is taken");
        } else {
            System.out.println("Enter password");
            String password = scanner.nextLine();
            Files.write(pathToUsersInfo, (name + "," + password + "\n").getBytes(), APPEND); //append part puts the"cursor" on to the new line so when we are inserting new info, then the last info won't be overwritten.
            System.out.println("User added: " + name);

        /*    Path path = Paths.get(name + ".txt");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, ("2" + "\n").getBytes(), APPEND);
            Files.write(path, (name + " messages" + "\n").getBytes(), APPEND);
           */

        }

    }

    public boolean isUserExisting(String name) throws IOException {
        List<String> usersInfo = Files.readAllLines(pathToUsersInfo);
        for (String user : usersInfo) {
            String[] split = user.split(",");

            if (split[0].equalsIgnoreCase(name)) {
                return true;
            }

        }
        return false;


    }

    public boolean logIn() throws IOException {
        List<String> usersInfo = Files.readAllLines(pathToUsersInfo);
        System.out.println("Enter your name");
        String name = scanner.nextLine();

        if (isUserExisting(name)) {
            for (String userInfo : usersInfo) {
                String[] split = userInfo.split(",");
                if (split[0].equalsIgnoreCase(name)) {
                    System.out.println("Enter password");
                    String password = scanner.nextLine();

                    if (split[1].equals(password)) {

                        System.out.println("you are logged in");
                        loggedInUser = split[0];
                        Files.write(pathToOnlineUsers, (loggedInUser + "\n").getBytes(), APPEND);
                        return true;

                    } else {
                        System.out.println("Wrong password");
                        //could add number of attempts
                        return false;
                    }


                }

            }


        } else {
            System.out.println("There is no user with this name");
            return false;
        }
        return false;
    }

    public void printUsers() throws IOException {
        List<String> usersInfo = Files.readAllLines(pathToUsersInfo);
        List<String> onlineUsers = Files.readAllLines(pathToOnlineUsers);

        for (String userInfo : usersInfo) {
            String[] split = userInfo.split(",");
            System.out.print(split[0]);
            if (onlineUsers.contains(split[0])) {
                System.out.print("(Online)");

            }
            System.out.print(", ");
        }
        System.out.println();
    }

    public int doesChatExsist(List<String> recipients) throws IOException {
        boolean allNamesInName = false;
        boolean nrOfCharacters = false;
        int rightChatNameNr = 0;

        int listSize = recipients.size();//nr of recipients
        int listNameCharAmount = loggedInUser.length() + listSize + 4;//+ listisuurus-1 kuna kui nt 4 inimest siis 3 space'i, +4 kuna .txt
        for (String name : recipients) {
            listNameCharAmount = listNameCharAmount + name.length();
        }

        List<String> createdChats = Files.readAllLines(pathToFileNames);
        for (int counter = 0; counter < createdChats.size(); counter++) {
            for (String name : recipients) {
                String chatName = createdChats.get(counter);
                if (chatName.contains(name)) {

                } else {
                    break;
                }
                //String path = "chats/" + createdChats.get(counter);
                if (recipients.get(recipients.size() - 1) == name) {
                    allNamesInName = true;
                    rightChatNameNr = counter;

                    if (chatName.length() == listNameCharAmount) {
                        nrOfCharacters = true;
                    }
                }

            }
        }

        if (allNamesInName && nrOfCharacters) {

            return rightChatNameNr;
        } else {
            return -1;
        }

    }


    public void sendANewMessage() throws IOException {
        List<String> recipients = new ArrayList<>();
        System.out.println("For whom do you want to send a message?");
        System.out.println("You can choose multiple people. \nif done=done");
        printUsers();
        //if (scanner.nextLine()=="") / kui esimeseks inimeseks kirjutab done
        while (true) {
            System.out.println("Enter a persons name.");
            String recipient = scanner.nextLine();

            if (recipient.equalsIgnoreCase("done") && recipients.size() == 0) {
                System.out.println("You have to enter at least one person.");
                break;
            }

            if (!recipient.equalsIgnoreCase("done")) {
                if (isUserExisting(recipient)) {
                    recipients.add(recipient);
                } else {
                    System.out.println("There is no user with this name");
                }

            } else {
                    //if chat exisits.
                int correctChat = doesChatExsist(recipients);
                if (correctChat != -1) {
                    List<String> createdChats = Files.readAllLines(pathToFileNames);
                    int chatNumber = correctChat;
                    String correctChatName = createdChats.get(chatNumber);
                    Path path = Paths.get("chats/" + correctChatName);


                    System.out.println("Write your message.");
                    String message = loggedInUser + " says: " + scanner.nextLine();
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ").format(new Date());
                    Files.write(path, (timeStamp + message + "\n").getBytes(), APPEND);
                    break;

                } else {
                    //if not exsists then it will create a new chat
                    String chatName = loggedInUser;
                    for (String name : recipients) {
                        chatName = chatName.concat("-" + name);
                    }
                    Path path = Paths.get("chats/" + chatName + ".txt");
                    Files.write(pathToFileNames, (chatName + ".txt" + "\n").getBytes(), APPEND); //append means to the end of file, \n means that a new line.

                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        //Files.write(path, ("2" + "\n").getBytes(), APPEND);
                        Files.write(path, ("Chat for: " + chatName.replace("-", ", ") + "\n").getBytes(), APPEND);

                    }

                    System.out.println("Write your message.");
                    String message = loggedInUser + " says: " + scanner.nextLine();
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ").format(new Date());
                    Files.write(path, (timeStamp + message + "\n").getBytes(), APPEND);
                    break;
                }
            }

        }
    }

//    public boolean haveNewMessages(List<String> messages) {
//        int listSize = messages.size();
//        int readMessagesCount = Integer.parseInt(messages.get(0));
//        if (listSize != readMessagesCount) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public void printOnlyNewMessages(Path path, List<String> messages) throws IOException {
//        int listSize = messages.size();
//       int readMessagesCount = Integer.parseInt(messages.get(0));
//
//        if (listSize != readMessagesCount) {
//            System.out.println(loggedInUser + " new messages:");
//            System.out.println(messages.get(1));
//            for (int counter = 0; counter < listSize; counter++) {
//                System.out.println(messages.get(counter));
//
//
//            }
//            messages.set(0, Integer.toString(listSize));
//            Files.write(path, messages);
//
//            adding et iga[ks n'eks eraldi et kas tal mingeid s]numeid avamata.
//        }

    public void readMessages() throws IOException {

        List<String> createdChats = Files.readAllLines(pathToFileNames);
        for (int counter = 0; counter < createdChats.size(); counter++) {
            if (createdChats.get(counter).contains(loggedInUser)) {
                Path path = Paths.get("chats/" + createdChats.get(counter));
                List<String> messages = Files.readAllLines(path);
                int listSize = messages.size();


                for (int counter2 = 0; counter2 < listSize; counter2++) {
                    System.out.println(messages.get(counter2));


                }
//                if (haveNewMessages(messages)) {
//                } else {
//
//
//                    else
//                } {
//                    for (String message : messages) {
//                        System.out.println(message);
//                    }
//
//                    System.out.println(messages.get(1));
//                    for (int counter2 = 2; counter2 < messages.size(); counter2++) {
//                        System.out.println(messages.get(counter2));
//
//
//                    }
//                    System.out.println();
//
//                }
            }
        }
        System.out.println("End of messages");

    }

    public void logOut() throws IOException {
        List<String> usersOnline = Files.readAllLines(pathToOnlineUsers);
        for (int counter = 0; counter < usersOnline.size(); counter++) {
            if (usersOnline.get(counter).equalsIgnoreCase(loggedInUser)) {
                usersOnline.set(counter, "");
                Files.write(pathToOnlineUsers, usersOnline);


            }
        }
    }
}

