

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


import static java.nio.file.StandardOpenOption.APPEND;


public class MessengerUtilities {

    public MessengerUtilities() throws IOException {
        if (!Files.exists(Paths.get("chats"))){
            Files.createDirectory(Paths.get("chats"));
        }

        if (!Files.exists(Paths.get("channels"))){
            Files.createDirectory(Paths.get("channels"));
        }

        if (!Files.exists(pathToUsersInfo)){
            Files.createFile(pathToUsersInfo);
        }
        if (!Files.exists(pathToFileNames)){
            Files.createFile(pathToFileNames);
        }
        if (!Files.exists(pathToOnlineUsers)){
            Files.createFile(pathToOnlineUsers);
        }
        if (!Files.exists(pathToChannels)){
            Files.createFile(pathToChannels);
        }


    }

    private Scanner scanner = new Scanner(System.in);
    private Path pathToUsersInfo = Paths.get("usersInfo.txt");
    private Path pathToFileNames = Paths.get("fileNames.txt");
    private Path pathToOnlineUsers = Paths.get("onlineUsers.txt");
    private Path pathToChannels = Paths.get("channels.txt");
    private String loggedInUser = "";


    void addUser() throws IOException {
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

    private boolean isUserExisting(String name) throws IOException {
        List<String> usersInfo = Files.readAllLines(pathToUsersInfo);
        for (String user : usersInfo) {
            String[] split = user.split(",");

            if (split[0].equalsIgnoreCase(name)) {
                return true;
            }

        }
        return false;


    }

    boolean logIn() throws IOException {

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
                        checksIfNewMessages t1 = new checksIfNewMessages();
                        new Thread(t1).start();
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

    //Prints out all the users and ads(Online) to those who are online.
    private void printUsers() throws IOException {
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

    //if exsists, then returns the location of the filename in fileNames.txt, otherwise returns -1
    private int doesChatExsist(List<String> recipients) throws IOException {
        boolean allNamesInName = false;
        boolean nrOfCharacters = false;
        int rightChatNameNr = 0;

        int listSize = recipients.size();//nr of recipients

        int listNameCharAmount = loggedInUser.length() + listSize;
        for (String name : recipients) {
            listNameCharAmount = listNameCharAmount + name.length();
        }

        List<String> createdChats = Files.readAllLines(pathToFileNames);
        for (int counter = 0; counter < createdChats.size(); counter++) {
            for (String name : recipients) {
                String chatName = createdChats.get(counter);
                if (!chatName.contains(name)) {
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

    void userWishesToSendANewMessage() throws IOException {
        List<String> recipients = new ArrayList<>();
        System.out.println("You can choose multiple people.");


        //TODO if i send a message, it is not unseen message for me
        //TODO already created chats to choose from
        //TODO if im in no chats then wont print. "created chats"
        while (true) {
            List<String> createdChats = Files.readAllLines(pathToFileNames);
            printUsers();
            if (!createdChats.isEmpty()) {
                System.out.print("Choose a exsisting chat: ");
                int chatNr = 0;
                for (String chatName : createdChats) {
                    if (chatName.contains(loggedInUser)) {
                        System.out.print(chatNr + "=" + chatName + ", ");
                        chatNr++;
                    }
                }
            }
            System.out.println("if done = done");
            System.out.println("Enter a persons name.");
            String userInput = scanner.nextLine().trim();


            if (userInput.equalsIgnoreCase("done") && recipients.size() == 0) {
                System.out.println("You have to enter at least one person.");
                break;
            }

            if (isUserExisting(userInput)) {
                recipients.add(userInput);

            } else if (userInput.matches("[0-9]+")) {
                int chosenChatNr = Integer.parseInt(userInput);
                Path path = Paths.get("chats/" + createdChats.get(chosenChatNr) + ".txt");
                sendingANewMessage(path);
                break;
            }


            if (userInput.equalsIgnoreCase("done")) {
                //checks if chat for those people already exsists.
                int correctChatNr = doesChatExsist(recipients);
                if (correctChatNr != -1) {
                    //List<String> createdChats = Files.readAllLines(pathToFileNames);

                    String correctChatName = createdChats.get(correctChatNr);
                    Path path = Paths.get("chats/" + correctChatName + ".txt");

                    sendingANewMessage(path);
                    break;
                }

                if (correctChatNr == -1) {
                    //if the chat with those people doesn't exsist, it will create one.
                    String chatName = loggedInUser;
                    for (String name : recipients) {
                        chatName = chatName.concat("-" + name);
                    }
                    Path path = Paths.get("chats/" + chatName + ".txt");
                    Files.write(pathToFileNames, (chatName + "\n").getBytes(), APPEND); //append means to the end of file, \n means that a new line.

                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        //adds to the first line 2's of the number of participants.

                        //Files.write(path,("\n").getBytes(),APPEND);
                        Files.write(path, ("3").getBytes(), APPEND);//for loggedinuser
                        for (int counter = 0; counter < recipients.size(); counter++) {//number 2's for others, until number of 2's=number of participants
                            Files.write(path, (" 3").getBytes(), APPEND);
                        }

                        Files.write(path, ("\nn").getBytes(), APPEND);//n for loggedinuser
                        for (int counter = 0; counter < recipients.size(); counter++) {//number n for others, n-no notification, y-yes
                            Files.write(path, (" n").getBytes(), APPEND);

                        }


                        Files.write(path, ("\nChat for: " + chatName.replace("-", ", ") + "\n").getBytes(), APPEND);

                    }

                    sendingANewMessage(path);
                    break;
                }
            }
        }
    }

    private List<String> getChats() throws IOException {

        List<String> createdChats = Files.readAllLines(pathToFileNames);
        List<String> iAmThere = new ArrayList<>();
        for (String chatName : createdChats) {
            if (chatName.contains(loggedInUser)) {
                iAmThere.add(chatName);
            }
        }
        return iAmThere;
    }

    private void sendingANewMessage(Path path) throws IOException {
        System.out.println("Write your message.");
        String message = loggedInUser + " says: " + scanner.nextLine();
        String timeStamp = new SimpleDateFormat("HH:mm:ss ").format(new Date()); //could add yyyy.MM.dd
        Files.write(path, (timeStamp + message + "\n").getBytes(), APPEND);
        List<String> allLines = Files.readAllLines(path);

        String[] secondLine = allLines.get(1).split(" ");
        String newSecondLine = "";
        for (int i = 0; i < secondLine.length; i++) {
            //if (i==returnMyNumberInFileNameAsIndex(chat))
            //TODO if i=mynumber then change it to "y " because i have seen my own message and i dont need a notification for it.
            newSecondLine = newSecondLine.concat("n ");
        }
        allLines.set(1, newSecondLine);
    }

    void readMessages() throws IOException {

        List<String> createdChats = Files.readAllLines(pathToFileNames);
        //it will add the empy line to the list
        boolean amIInAnyChat = false;
        for (int counter = 0; counter < createdChats.size(); counter++) {
            String chatNameWhereIAm = createdChats.get(counter);
            if (chatNameWhereIAm.contains(loggedInUser)) {
                amIInAnyChat = true;
                String[] recipientsNames = chatNameWhereIAm.split("-");

                int whichOneAmI = returnMyNumberInFileNameAsIndex(chatNameWhereIAm);

                Path path = Paths.get("chats/" + chatNameWhereIAm + ".txt");
                List<String> messages = Files.readAllLines(path);


                int listSize = messages.size();

                String[] firstLineNumbers = messages.get(0).split(" ");


                String myReadMessageNr = firstLineNumbers[whichOneAmI];
                int myNumber = Integer.parseInt(myReadMessageNr);


                for (int count = 2; count < listSize; count++) {

                    if (myNumber > count) {
                        System.out.println(messages.get(count));
                    } else {
                        System.out.println("(new)" + messages.get(count));
                    }
                }
                changeMyNumberInFirstLine(chatNameWhereIAm);
                changeMyNotificationStatusTo(chatNameWhereIAm, "n");
            }

        }
        if (!amIInAnyChat) {
            System.out.println("You are not in any chat.");
        }
    }

    void logOut() throws IOException {
        List<String> usersOnline = Files.readAllLines(pathToOnlineUsers);
        for (int counter = 0; counter < usersOnline.size(); counter++) {
            if (usersOnline.get(counter).equalsIgnoreCase(loggedInUser)) {
                usersOnline.set(counter, "");
                Files.write(pathToOnlineUsers, usersOnline);
                loggedInUser = "";
            }
        }
    }

    void deleteAllMsnInfo() throws IOException {

        PrintWriter pw = new PrintWriter("usersInfo.txt");
        PrintWriter pw1 = new PrintWriter("fileNames.txt");
        PrintWriter pw2 = new PrintWriter("onlineUsers.txt");
        PrintWriter pw3 = new PrintWriter("channels.txt");

        Files.walk(Paths.get("chats/"))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                //.peek(System.out::println)
                .forEach(it -> {
                    if (it.isDirectory()) {

                    } else {
                        it.delete();
                    }
                });

        Files.walk(Paths.get("channels/"))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                //.peek(System.out::println)
                .forEach(it -> {
                    if (it.isDirectory()) {

                    } else {
                        it.delete();
                    }
                });

    }

    private void newMessagesNotification() throws IOException, InterruptedException {

        List<String> myChats = getChats();
        //String newMessagesIn = "";
        List<String> sentNotification = new ArrayList<>();


        for (String chatName : myChats) {
            Path path = Paths.get("chats/" + chatName + ".txt");
            if (Files.exists(path)) {//added if exsists
                Thread.sleep(2000);

                boolean b = returnTrueIfNotificationIsSent(chatName);
                if (!b) {
                    int myPlaceInFirstLine = returnMyNumberInFileNameAsIndex(chatName);
                    List<String> allLines = Files.readAllLines(path);
                    String[] firstLine = allLines.get(0).split(" ");


                    if (Integer.parseInt(firstLine[myPlaceInFirstLine]) != allLines.size()) {
                        //newMessagesIn = newMessagesIn.concat(chatName + ", ");
                        sentNotification.add(chatName);

                    }

                }

            }
        }
        if (!sentNotification.isEmpty()) {
            for (String chatName : sentNotification) {
                changeMyNotificationStatusTo(chatName, "y");
            }
        }
        //if (!newMessagesIn.equalsIgnoreCase("")) {
        if (!sentNotification.isEmpty()) {
            System.out.println("New messages in " + String.join(", ", sentNotification));

        }
    }

    private int returnMyNumberInFileNameAsIndex(String chatName) {
        String[] fileName = chatName.split("-");
        for (int i = 0; i < fileName.length; i++) {
            if (fileName[i].contains(loggedInUser)) {
                return i;
            }
        }
        return -1;
    }

    private boolean returnTrueIfNotificationIsSent(String chatName) throws IOException {
        Path path = Paths.get("chats/" + chatName + ".txt");
        List<String> allLines = Files.readAllLines(path);
        String[] secondLine = allLines.get(1).split(" ");
        int myNotification = returnMyNumberInFileNameAsIndex(chatName);

        if (secondLine[myNotification].equalsIgnoreCase("n")) {
            return false;
        } else {
            return true;
        }


    }

    //changes my number in to first line to filesize so i have read all
    private void changeMyNumberInFirstLine(String chatName) throws IOException {

        int myPlaceInFirstLine = returnMyNumberInFileNameAsIndex(chatName);
        Path path = Paths.get("chats/" + chatName + ".txt");

        List<String> allLines = Files.readAllLines(path);
        String[] firstLine = allLines.get(0).split(" ");

        firstLine[myPlaceInFirstLine] = Integer.toString(allLines.size());
        /*String newFirstLine = "";
        for (int i = 0; i < firstLine.length; i++) {
            newFirstLine = newFirstLine.concat(firstLine[i]) + " ";
        }*/

        String newFirstLine = String.join("-", firstLine);

        allLines.set(0, newFirstLine);
        Files.write(path, (allLines));
    }

    private void changeMyNotificationStatusTo(String chatName, String yORn) throws IOException {
        int myPlaceInSecondLine = returnMyNumberInFileNameAsIndex(chatName);
        Path path = Paths.get("chats/" + chatName + ".txt");

        List<String> allLines = Files.readAllLines(path);
        String[] secondLine = allLines.get(1).split(" ");

        secondLine[myPlaceInSecondLine] = yORn;
        String newSecondLine = String.join(" ", secondLine);
       /* String newSecondLine = "";
        for (int i = 0; i < secondLine.length; i++) {
            newSecondLine = newSecondLine.concat(secondLine[i]) + " ";
        }*/

        allLines.set(1, newSecondLine);
        Files.write(path, (allLines));

    }

    class checksIfNewMessages implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    newMessagesNotification();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void createANewChannel(List<String> recipients) throws IOException {
        String channelName = String.join("-", recipients);
        Files.write(pathToChannels, channelName.getBytes(), APPEND);
        Path pathToChannel = Paths.get("channels/" + channelName + ".txt");
        if (!Files.exists(pathToChannel)) {
            Files.createFile(pathToChannel);
        }



    }


}
