
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


import static java.nio.file.Files.readAllLines;
import static java.nio.file.StandardOpenOption.APPEND;


class MessengerUtilities {

    MessengerUtilities() throws IOException {
        if (!Files.exists(Paths.get("chats"))) {
            Files.createDirectory(Paths.get("chats"));
        }

        if (!Files.exists(Paths.get("publicChannels"))) {
            Files.createDirectory(Paths.get("publicChannels"));
        }

        if (!Files.exists(Paths.get("privateChannels"))) {
            Files.createDirectory(Paths.get("privateChannels"));
        }

        if (!Files.exists(pathToUsersInfo)) {
            Files.createFile(pathToUsersInfo);
        }
        if (!Files.exists(pathToFileNames)) {
            Files.createFile(pathToFileNames);
        }
        if (!Files.exists(pathToOnlineUsers)) {
            Files.createFile(pathToOnlineUsers);
        }
        if (!Files.exists(pathToPublicChannels)) {
            Files.createFile(pathToPublicChannels);
        }
        if (!Files.exists(pathToPrivateChannels)) {
            Files.createFile(pathToPrivateChannels);
        }


    }

    private Scanner scanner = new Scanner(System.in);
    private Path pathToUsersInfo = Paths.get("usersInfo.txt");
    private Path pathToFileNames = Paths.get("fileNames.txt");
    private Path pathToOnlineUsers = Paths.get("onlineUsers.txt");
    private Path pathToPublicChannels = Paths.get("publicChannels.txt");
    private Path pathToPrivateChannels = Paths.get("privateChannels.txt");
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
        List<String> usersInfo = readAllLines(pathToUsersInfo);
        for (String user : usersInfo) {
            String[] split = user.split(",");

            if (split[0].equalsIgnoreCase(name)) {
                return true;
            }

        }
        return false;


    }

    boolean logIn() throws IOException {

        List<String> usersInfo = readAllLines(pathToUsersInfo);
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
                    /*    checksIfNewMessages t1 = new checksIfNewMessages();
                        new Thread(t1).start();*/ // 1 way to do it

                     /*   Thread checksIfNewMessages = new Thread(new checksIfNewMessages()); ///////////////////
                        checksIfNewMessages.start();*/ // second way to do it
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
        List<String> usersInfo = readAllLines(pathToUsersInfo);
        List<String> onlineUsers = readAllLines(pathToOnlineUsers);

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

        List<String> createdChats = readAllLines(pathToFileNames);
        for (int counter = 0; counter < createdChats.size(); counter++) {
            for (String name : recipients) {
                String chatName = createdChats.get(counter);
                if (!chatName.contains(name)) {
                    break;
                }

                //String path = "chats/" + createdChats.get(counter);
                if (recipients.get(recipients.size() - 1).equals(name)) {
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

        while (true) {
            List<String> createdChats = readAllLines(pathToFileNames);
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
            } else{
                System.out.println("No such a user.");
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

        List<String> createdChats = readAllLines(pathToFileNames);
        List<String> iAmThere = new ArrayList<>();
        for (String chatName : createdChats) {
            if (chatName.contains(loggedInUser)) {
                iAmThere.add(chatName);
            }
        }
        return iAmThere;
    }

    private List<String> getAllPublicChannels() throws IOException {

        List<String> createdChannels = readAllLines(pathToPublicChannels);
        List<String> allPublicChannels = new ArrayList<>();
        for (String chatName : createdChannels) {
            String[] chat = chatName.split("//");
            chatName = chat[0];
            allPublicChannels.add(chatName);
        }
        return allPublicChannels;
    }

    private List<String> getMyPublicChannels() throws IOException {

        List<String> createdChannels = readAllLines(pathToPublicChannels);
        List<String> allMyPublicChannels = new ArrayList<>();
        for (String chatName : createdChannels) {
            if (chatName.contains(loggedInUser)) {
                String[] chat = chatName.split("//");
                chatName = chat[0];
                allMyPublicChannels.add(chatName);
            }
        }
        return allMyPublicChannels;
    }

    private List<String> getPrivateChannels() throws IOException {

        List<String> createdChannels = readAllLines(pathToPrivateChannels);
        List<String> allPrivateChannels = new ArrayList<>();
        for (String chatName : createdChannels) {
            if (chatName.contains(loggedInUser)) {
                String[] chat = chatName.split("//");
                chatName = chat[0];
                allPrivateChannels.add(chatName);
            }
        }
        return allPrivateChannels;
    }

    private void sendingANewMessage(Path path) throws IOException {
        System.out.println("Write your message.");
        String message = loggedInUser + " says: " + scanner.nextLine();
        String timeStamp = new SimpleDateFormat("HH:mm:ss ").format(new Date()); //could add yyyy.MM.dd
        Files.write(path, (timeStamp + message + "\n").getBytes(), APPEND);
        List<String> allLines = readAllLines(path);
        String fileName = path.toString();
        int kaldkriipsuIndeks = fileName.indexOf("/");
        int punktiIndeks = fileName.indexOf(".");
        fileName = fileName.substring(kaldkriipsuIndeks, punktiIndeks);


        String[] secondLine = allLines.get(1).split(" ");
        String newSecondLine = "";
        for (int i = 0; i < secondLine.length; i++) {
            if (i == returnMyNumberInFileNameAsIndex(fileName)) {
                newSecondLine = newSecondLine.concat("y ");
            } else {
                newSecondLine = newSecondLine.concat("n ");
            }
            //if i=mynumber then change it to "y " because i have seen my own message and i dont need a notification for it.
        }
        allLines.set(1, newSecondLine);
        Files.write(path, (allLines));
        if (path.toString().contains("chats")) {
            changeMyNumberInFirstLine(fileName, "chats");
        } else if (path.toString().contains("publicChannels")) {
            changeMyNumberInFirstLine(fileName, "publicChannels");
        } else if (path.toString().contains("privateChannels")) {
            changeMyNumberInFirstLine(fileName, "privateChannels");
        }
    }

    void readMessages() throws IOException {

        List<String> createdChats = readAllLines(pathToFileNames);
        //it will add the empy line to the list
        boolean amIInAnyChat = false;
        for (int counter = 0; counter < createdChats.size(); counter++) {
            String chatNameWhereIAm = createdChats.get(counter);
            if (chatNameWhereIAm.contains(loggedInUser)) {
                amIInAnyChat = true;


                int whichOneAmI = returnMyNumberInFileNameAsIndex(chatNameWhereIAm);

                Path path = Paths.get("chats/" + chatNameWhereIAm + ".txt");
                List<String> messages = readAllLines(path);


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
                changeMyNumberInFirstLine(chatNameWhereIAm, "chats");
                changeMyNotificationStatusTo(chatNameWhereIAm, "n");
            }

        }
        if (!amIInAnyChat) {
            System.out.println("You are not in any chat.");
        }
    }

    void logOut() throws IOException {
        List<String> usersOnline = readAllLines(pathToOnlineUsers);
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
        PrintWriter pw3 = new PrintWriter("publicChannels.txt");
        PrintWriter pw4 = new PrintWriter("privateChannels.txt");

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

        Files.walk(Paths.get("privateChannels/"))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                //.peek(System.out::println)
                .forEach(it -> {
                    if (it.isDirectory()) {

                    } else {
                        it.delete();
                    }
                });

        Files.walk(Paths.get("publicChannels/"))
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
        List<String> myPublicChannels = getMyPublicChannels();
        List<String> myPrivateChannels = getPrivateChannels();
        List<String> sentNotification = new ArrayList<>();


        for (String chatName : myChats) {
            Path path = Paths.get("chats/" + chatName + ".txt");
            if (Files.exists(path)) {//added if exsists
                Thread.sleep(2000);
                boolean b = returnTrueIfNotificationIsSent(chatName, "chats");
                if (!b) {
                    int myPlaceInFirstLine = returnMyNumberInFileNameAsIndex(chatName);
                    List<String> allLines = readAllLines(path);
                    String[] firstLine = allLines.get(0).split(" ");


                    if (Integer.parseInt(firstLine[myPlaceInFirstLine]) != allLines.size()) {
                        //newMessagesIn = newMessagesIn.concat(chatName + ", ");
                        sentNotification.add(chatName);

                    }

                }

            }
        }

        for (String channelName : myPublicChannels) {
            Path path = Paths.get("publicChannels/" + channelName + ".txt");
            if (Files.exists(path)) {
                boolean b = returnTrueIfNotificationIsSent(channelName, "publicChannels");
                if (!b) {
                    List<String> allLines = readAllLines(path);
                    String[] fourthLine = allLines.get(3).split(" ");
                    int myPlaceInFourthLine = 0;
                    for (int i = 0; i < fourthLine.length; i++) {
                        if (fourthLine[i].equalsIgnoreCase(loggedInUser)) {
                            myPlaceInFourthLine = i;
                        }
                    }

                    if (Integer.parseInt(fourthLine[myPlaceInFourthLine]) != allLines.size()) {

                        sentNotification.add(channelName);

                    }

                }

            }


        }

        for (String channelName : myPrivateChannels) {
            Path path = Paths.get("privateChannels/" + channelName + ".txt");
            if (Files.exists(path)) {
                boolean b = returnTrueIfNotificationIsSent(channelName, "privateChannels");
                if (!b) {
                    List<String> allLines = readAllLines(path);
                    String[] fourthLine = allLines.get(3).split(" ");
                    int myPlaceInFourthLine = 0;
                    for (int i = 0; i < fourthLine.length; i++) {
                        if (fourthLine[i].equalsIgnoreCase(loggedInUser)) {
                            myPlaceInFourthLine = i;
                        }
                    }

                    if (Integer.parseInt(fourthLine[myPlaceInFourthLine]) != allLines.size()) {

                        sentNotification.add(channelName);

                    }

                }

            }

        }

        if (!sentNotification.isEmpty()) {
            for (String chatName : sentNotification) {
                changeMyNotificationStatusTo(chatName, "y");
            }
            for (String channelName : myPublicChannels) {
                changeMyNotificationStatusTo(channelName, "y");
            }
            for (String channelName : myPrivateChannels) {
                changeMyNotificationStatusTo(channelName, "y");
            }

            System.out.println("New messages in " + String.join(", ", sentNotification));
        }
    }

    private int returnMyNumberInFileNameAsIndex(String chatName) throws IOException {
        List<String> myChats = getChats();
        if (myChats.contains(chatName)) {
            String[] fileName = chatName.split("-");
            for (int i = 0; i < fileName.length; i++) {
                if (fileName[i].contains(loggedInUser)) {
                    return i;
                }
            }
        }

        List<String> myPublicChannels = getMyPublicChannels();
        for (int i = 0; i < myPublicChannels.size(); i++) {
            if (myPublicChannels.get(i).contains(loggedInUser)) {
                String[] namesList = myPublicChannels.get(i).split(" ");
                for (int j = 0; j < namesList.length; j++) {
                    if (namesList[j].equalsIgnoreCase(loggedInUser)) {
                        return j;


                    }

                }


            }

        }

        List<String> myPrivateChannels = getPrivateChannels();
        for (int i = 0; i < myPrivateChannels.size(); i++) {
            if (myPrivateChannels.get(i).contains(loggedInUser)) {
                String[] namesList = myPrivateChannels.get(i).split(" ");
                for (int j = 0; j < namesList.length; j++) {
                    if (namesList[j].equalsIgnoreCase(loggedInUser)) {
                        return j;


                    }

                }

            }

        }
        return -1;
    }

    private boolean returnTrueIfNotificationIsSent(String chatName, String folderName) throws IOException {
        Path path = Paths.get(folderName + "/" + chatName + ".txt");
        List<String> allLines = readAllLines(path);
        String[] secondLine = allLines.get(1).split(" ");
        int myNotification = returnMyNumberInFileNameAsIndex(chatName);

        if (secondLine[myNotification].equalsIgnoreCase("n")) {
            return false;
        } else {
            return true;
        }


    }

    //changes my number in to first line to filesize so i have read all
    private void changeMyNumberInFirstLine(String chatName, String folderName) throws IOException {
        if (folderName.equalsIgnoreCase("chats")) {
            int myPlaceInFirstLine = returnMyNumberInFileNameAsIndex(chatName);
            Path path = Paths.get(folderName + "/" + chatName + ".txt");

            List<String> allLines = readAllLines(path);
            String[] firstLine = allLines.get(0).split(" ");

            firstLine[myPlaceInFirstLine] = Integer.toString(allLines.size());


            String newFirstLine = String.join(" ", firstLine);

            allLines.set(0, newFirstLine);
            Files.write(path, (allLines));
        }
        if (folderName.contains("Channels")) {
            Path path = Paths.get(folderName + "/" + chatName + ".txt");

            List<String> allLines = readAllLines(path);
            String[] fourthLine = allLines.get(3).split(" ");
            int myPlaceInFourthLine = 0;
            for (int i = 0; i < fourthLine.length; i++) {
                if (fourthLine[i].equalsIgnoreCase(loggedInUser)) {
                    myPlaceInFourthLine = i;
                }

            }

            fourthLine[myPlaceInFourthLine] = Integer.toString(allLines.size());

            String newFirstLine = String.join(" ", fourthLine);

            allLines.set(0, newFirstLine);
            Files.write(path, (allLines));
        }

    }

    private void changeMyNotificationStatusTo(String chatName, String yORn) throws IOException {
        int myPlaceInSecondLine = returnMyNumberInFileNameAsIndex(chatName);
        Path path = Paths.get("chats/" + chatName + ".txt");

        List<String> allLines = readAllLines(path);
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

    private String returnChannelPathAsStringWhenIHaveChosenMyChannel(String channelName) throws IOException {
        List<String> publicChannels = readAllLines(pathToPublicChannels);

        for (String chatName : publicChannels) {
            if (chatName.contains(channelName)) {
                String[] nameLine = chatName.split("//");
                chatName = nameLine[0];
                return "publicChannels/" + chatName + ".txt";

            }
        }
        List<String> privateChannels = readAllLines(pathToPrivateChannels);

        for (String chatName : privateChannels) {
            if (chatName.contains(channelName)) {
                String[] nameLine = chatName.split("//");
                chatName = nameLine[0];
                return "privateChannels/" + chatName + ".txt";

            }
        }


        return "no such a channel";


    }

    void userEntersChannelMenu() throws IOException {
        while (true) {
            System.out.println("What do you want to do? \n[0]Show my channels, [1]Open public channels,\n[2]Create a new channel, [3]Back to main menu ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("0")) {
                if (!getAllPublicChannels().isEmpty()) {
                    System.out.println("Public: " + getMyPublicChannels().toString());
                }
                if (!getPrivateChannels().isEmpty()) {
                    System.out.println("Private: " + getPrivateChannels().toString());
                }
                if (getPrivateChannels().isEmpty() && getAllPublicChannels().isEmpty()) {
                    System.out.println("You are not in any channel");
                } else {
                    System.out.println("Do you want to send a message to any channel? [0]Yes, [1]No");
                    while (true) {
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("0")) {
                            System.out.println("Write your channel name");
                            String channelName = scanner.nextLine();
                            Path pathToCorrectChannel = Paths.get(returnChannelPathAsStringWhenIHaveChosenMyChannel(channelName));

                            String pathAsString = pathToCorrectChannel.toString();
                            if (pathAsString.contains("publicChannels")) {
                                readChannelContentWithFilename(channelName, "publicChannels");
                            } else if (pathAsString.contains("privateChannels")) {
                                readChannelContentWithFilename(channelName, "privateChannels");
                            }
                            sendingANewMessage(pathToCorrectChannel);

                        } else if (input.equalsIgnoreCase("1")) {
                            break;
                        }
                    }
                }
            } else if (userInput.equalsIgnoreCase("1")) {
                getAllPublicChannels().toString();


                while (true) {

                    List<String> publicChannels = getAllPublicChannels();
                    List<String> myPublicChannels = getMyPublicChannels();

                    for (String channelName : myPublicChannels) {
                        publicChannels.remove(channelName);
                    }
                    if (!publicChannels.isEmpty()) {
                        System.out.println("Choose a public channel to join.");
                        System.out.println(publicChannels.toString());

                        while (true) {
                            String input = scanner.nextLine();
                            if (publicChannels.contains(input)) {
                                Path path = Paths.get(returnChannelPathAsStringWhenIHaveChosenMyChannel(input));

                                readChannelContentWithFilename(input, "publicChannels");
                                System.out.println("Do you want to send a message to " + input);
                                System.out.println("[0]yes, [1]No");
                                String yesOrNo = scanner.nextLine();
                                if (yesOrNo.equalsIgnoreCase("0")) {
                                    sendingANewMessage(path);
                                } else if (yesOrNo.equalsIgnoreCase("1")) {
                                    break;
                                }
                            } else {
                                System.out.println("No such a channel.");
                                break;
                            }
                        }
                    } else {
                        System.out.println("No channel to join.");
                        break;
                    }
                }
            } else if (userInput.equalsIgnoreCase("2")) {
                createANewChannel();

            } else if (userInput.equalsIgnoreCase("3")) {
                break;
            } else {
                System.out.println("You need to choose between given commands.");
            }
        }
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

    private void createANewChannel() throws IOException {


        System.out.println("Do you want this channel to be [0]private, [1]public");
        String privateOrPublic = scanner.nextLine();
        System.out.println("Choose a name for this channel.");
        Path pathToChannel = Paths.get("");
        String channelName = "";
        while (true) {
            channelName = scanner.nextLine();
            if (privateOrPublic.equalsIgnoreCase("1")) {
                pathToChannel = Paths.get("publicChannels/" + channelName + ".txt");
                break;
            } else if (privateOrPublic.equalsIgnoreCase("0")) {
                pathToChannel = Paths.get("privateChannels/" + channelName + ".txt");
                break;
            } else {
                System.out.println("choose either a public or private");
            }
        }

        if (!Files.exists(pathToChannel)) {

            if (privateOrPublic.equalsIgnoreCase("0")) {
                System.out.println("You have to add some users to your Private channel. [done]Done");
                printUsers();
                List<String> userInPrivateChannel = new ArrayList<>();
                userInPrivateChannel.add(loggedInUser);

                while (true) {
                    String addingUserToPrivateChannel = scanner.nextLine();
                    if (userInPrivateChannel.size() < 2 && addingUserToPrivateChannel.equalsIgnoreCase("done")) {
                        System.out.println("Add at least 2 users");
                    } else if (addingUserToPrivateChannel.equalsIgnoreCase("done")) {
                        break;
                    } else if (isUserExisting(addingUserToPrivateChannel)) {
                        userInPrivateChannel.add(addingUserToPrivateChannel);
                        System.out.println(addingUserToPrivateChannel + " added.");
                    } else {
                        System.out.println("There is no " + addingUserToPrivateChannel);
                    }
                }


                Files.write(pathToPrivateChannels, (channelName + "// " + String.join(" ", userInPrivateChannel) + "\n").getBytes(), APPEND);
                Files.createFile(pathToChannel);


                for (int i = 0; i < userInPrivateChannel.size(); i++) {
                    Files.write(pathToChannel, ("4 ").getBytes(), APPEND);
                }
                Files.write(pathToChannel, ("\n").getBytes(), APPEND);
                for (int i = 0; i < userInPrivateChannel.size(); i++) {
                    Files.write(pathToChannel, ("n ").getBytes(), APPEND);
                }
                Files.write(pathToChannel, ("\n").getBytes(), APPEND);

                Files.write(pathToChannel, (channelName + "// members: \n" + String.join(" ", userInPrivateChannel) + "\n").getBytes(), APPEND);

                System.out.println("Do you want to send a message to? " + channelName + " [0]Yes, [1]No");
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("0")) {
                        sendingANewMessage(pathToChannel);
                        break;
                    } else if (input.equalsIgnoreCase("1")) {
                        break;
                    }
                }


            } else if (privateOrPublic.equalsIgnoreCase("1")) {
                System.out.println("Do you want to add some users to your Public Channel? [done]Done");
                printUsers();
                List<String> userInPublicChannel = new ArrayList<>();
                userInPublicChannel.add(loggedInUser);

                while (true) {
                    String addingUserToPublicChannel = scanner.nextLine();
                    if (addingUserToPublicChannel.equalsIgnoreCase("done")) {
                        break;
                    }
                    if (isUserExisting(addingUserToPublicChannel)) {
                        userInPublicChannel.add(addingUserToPublicChannel);
                        System.out.println(addingUserToPublicChannel + " added.");
                    } else {
                        System.out.println("There is no " + addingUserToPublicChannel);
                    }
                }

                Files.write(pathToPublicChannels, (channelName + "// " + String.join(" ", userInPublicChannel) + "\n").getBytes(), APPEND);
                Files.createFile(pathToChannel);

                for (int i = 0; i < userInPublicChannel.size(); i++) {
                    Files.write(pathToChannel, ("4 ").getBytes(), APPEND);
                }
                Files.write(pathToChannel, ("\n").getBytes(), APPEND);
                for (int i = 0; i < userInPublicChannel.size(); i++) {
                    Files.write(pathToChannel, ("n ").getBytes(), APPEND);
                }
                Files.write(pathToChannel, ("\n").getBytes(), APPEND);

                Files.write(pathToChannel, (channelName + "// members: \n" + String.join(" ", userInPublicChannel)).getBytes(), APPEND);


                System.out.println("Do you want to send a message to? " + channelName + " [0]Yes, [1]No");
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("0")) {
                        sendingANewMessage(pathToChannel);
                        break;
                    } else if (input.equalsIgnoreCase("1")) {
                        break;
                    }
                }

            }
        } else {
            System.out.println("channel with this exact name exists.");
        }
    }

    private void readChannelContentWithFilename(String fileName, String folderName) throws IOException {


        Path path = Paths.get(folderName + "/" + fileName + ".txt");
        List<String> messages = readAllLines(path);
        int listSize = messages.size();
        String[] names = messages.get(3).split(" ");
        int whichOneAmI = 0;

        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(loggedInUser)) {
                whichOneAmI = i;
            }
        }


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
        changeMyNumberInFirstLine(fileName, folderName);
        changeMyNotificationStatusTo(fileName, "n");
    }


}
