import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class App {

    // method to get string input
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    // method to get int input
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
        } catch (NumberFormatException e) {
        }
        return i;
    }

    // method to format date input
    public static String formatDate(String date) {
        String[] parts = date.split("-");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        String[] monthNames = {
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
        };

        String dayString = String.valueOf(day);
        String monthString = monthNames[month - 1];
        String yearString = String.valueOf(year).substring(2);

        return dayString + "-" + monthString + "-" + yearString;
    }

    // method to validate yes or no answers
    public static String validate(String ans, BufferedReader in) {
        while (!ans.toUpperCase().equals("Y") && !ans.toUpperCase().equals("N")) {
            System.out.println("Invalid input. Please try again");
            ans = getString(in, "Enter Y for yes, N for no");

        }
        return ans;
    }

    public static void main(String[] args)
            throws Exception, SQLException, IOException, java.lang.ClassNotFoundException {

        // getting user input for username and password
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String id = getString(in, "Enter username");
        String password = getString(in, "Enter password");

        // connecting to the database
        try (
                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", id,
                        password);) {
            System.out.println("connection successfully made.");
            Statement s = con.createStatement();
            String ldregex = "^\\d+\\s+[a-zA-Z\\s]+$";
            String lregex = "^[a-zA-Z\\s]+$";
            String regex = "[\\d-]+";
            String eregex = "^(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}$";
            int inter;
            while (true) {

                inter = getInt(in,
                        "\nWelcome to the Hotel California Database\nWould you like to access a: \n1: The Customer interface\n2: The Employee interface\n3: Exit");

                if (inter == 1) {

                    int hotel;
                    hotel = getInt(in,
                            "\nThank you for choosing Hotel California Enterprises \n Please Select one of our Hotel Destinations \n(Enter 0 if you would like to view hotel amenities before making a selection) \n1: Tropic - Miami, Florida \n2: Magnificent - Seattle, Washington \n3: Glorious - Philadelphia, Pennsylvania \n4: Grand Estate - Detroit Michigan \n5: Presidental  - Washington, DC \n6: Occidental - Charleston, South Carolina \n7: Atlantic - Atlanta, Georgia \n8: Golden - Las Vegas, Nevada \n9: Pacific - San Francisco, California \n10: Islander - New York City, New York\n");

                    while (hotel < 1 || hotel > 10) {
                        if (hotel == 0) {
                            int ahot = getInt(in,
                                    "Please enter what hotel you would like to view amenities for (1-10):");
                            while (ahot < 1 || ahot > 10) {
                                System.out.println("Invalid hotel choice. Please try again.");
                                ahot = getInt(in, "Select a hotel beween 1 and 10");
                            }
                            PreparedStatement amen = con
                                    .prepareStatement("Select amenity_name from amenities where hotel_id = ?");
                            amen.setInt(1, ahot);

                            ResultSet amenities = amen.executeQuery();
                            if (!amenities.next()) {
                                System.out.println("\nThis hotel does not offer any amenities");
                            } else {
                                System.out.println("\nAmenities at this hotel include:");
                                do {
                                    System.out.println(amenities.getString("amenity_name"));

                                } while (amenities.next());
                            }

                            int choose = getInt(in,
                                    "\nWould you like to\n1: Pick this hotel\n2: Look at other options");
                            if (choose == 1) {
                                hotel = ahot;
                            } else if (choose == 2) {
                                hotel = getInt(in,
                                        "\nPlease Select one of our Hotel Destinations \n(Enter 0 if you would like to view hotel amenities before making a selection) \n1: Tropic - Miami, Florida \n2: Magnificent - Seattle, Washington \n3: Glorious - Philadelphia, Pennsylvania \n4: Grand Estate - Detroit Michigan \n5: Presidental  - Washington, DC \n6: Occidental - Charleston, South Carolina \n7: Atlantic - Atlanta, Georgia \n8: Golden - Las Vegas, Nevada \n9: Pacific - San Francisco, California \n10: Islander - New York City, New York\n");
                            } else {
                                while (choose != 1 && choose != 2) {
                                    System.out.println("Invalid option. Please try again");
                                    choose = getInt(in,
                                            "Would you like to\n1: Pick this hotel\n2: Look at other options");
                                }
                            }

                        } else {
                            System.out.println("Invalid hotel choice. Please try again.");
                            hotel = getInt(in, "Select a hotel beween 1 and 10");
                        }

                    }
                    String start = getString(in, "Please enter your arrival date in form mm-dd-yyyy");
                    String end = getString(in, "Please enter your departure date in form mm-dd-yyyy");
                    boolean validDay = false;
                    while (!validDay) {
                        String DATE_PATTERN = "^\\d{2}-\\d{2}-\\d{4}$";
                        LocalDate today = LocalDate.now();
                        LocalDate max = today.plusYears(3);
                        int maxDif = 30;
                        if (!start.matches(DATE_PATTERN) || !end.matches(DATE_PATTERN)) {
                            if (!start.matches(DATE_PATTERN)) {
                                System.out.println("Start date not added in correct format. Please try again");
                            } else if (!end.matches(DATE_PATTERN)) {
                                System.out.println("End date not added in correct format. Please try again");
                            }

                        } else {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                                LocalDate sdate = LocalDate.parse(start, formatter);
                                LocalDate edate = LocalDate.parse(end, formatter);

                                if (!sdate.isAfter(today)) {
                                    System.out.println("Start date must be in the future. Please try again");
                                } else if (sdate.isAfter(max) || edate.isAfter(max)) {
                                    System.out.println(
                                            "Cannot make reservation more than 3 years in adavance. Please try agian.");
                                } else {
                                    if (!edate.isAfter(sdate)) {
                                        System.out.println("End date must be after start date. Please try again");
                                    } else if (!sdate.plusDays(maxDif).isAfter(edate)) {
                                        System.out.println("Stay cannot exceed 30 days. Please try again");
                                    } else {
                                        validDay = true;
                                        break;
                                    }
                                }
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date. Please try again");
                                validDay = false;
                            }
                        }
                        start = getString(in, "Please enter your arrival date in form mm-dd-yyyy");
                        end = getString(in, "Please enter your departure date in form mm-dd-yyyy");
                    }

                    start = formatDate(start);
                    end = formatDate(end);

                    ArrayList<String> rooms = new ArrayList<String>();
                    PreparedStatement getAvalability = con.prepareStatement(
                            "SELECT 50 - count(room_type) as count FROM reservation WHERE hotel_id = ? and room_type = ? and start_date <= ? AND end_date >= ?");
                    getAvalability.setInt(1, hotel);
                    getAvalability.setString(2, "Queen");
                    getAvalability.setString(3, start);
                    getAvalability.setString(4, end);

                    ResultSet queens = getAvalability.executeQuery();
                    queens.next();
                    if (queens.getInt("count") > 0) {
                        rooms.add("Queen");
                    }

                    getAvalability.setString(2, "King");
                    ResultSet kings = getAvalability.executeQuery();
                    kings.next();
                    if (kings.getInt("count") > 0) {
                        rooms.add("King");
                    }

                    getAvalability.setString(2, "Double");
                    ResultSet doubless = getAvalability.executeQuery();
                    doubless.next();
                    if (doubless.getInt("count") > 0) {
                        rooms.add("Double");
                    }

                    System.out.println(
                            "\nHere are the avaialable room types for the selected dates.");
                    for (String room : rooms) {
                        System.out.println(room);
                    }

                    String choice = getString(in, "Please select a room type:");
                    while (!rooms.contains(choice)) {

                        System.out.println("Invalid room choice. Please try again");
                        choice = getString(in, "Please select a room type:");

                    }

                    PreparedStatement getBase = con
                            .prepareStatement("SELECT base_rate from roomTypes where room_type = ?");
                    getBase.setString(1, choice);
                    ResultSet base = getBase.executeQuery();
                    base.next();

                    PreparedStatement getRate = con.prepareStatement(
                            "SELECT sum(dollar_price) AS total_cost, count(date1) as days FROM rates WHERE date1 >= ? AND date1 < ?");
                    getRate.setString(1, start);
                    getRate.setString(2, end);

                    ResultSet rate = getRate.executeQuery();
                    rate.next();

                    int days = rate.getInt("days");
                    double total = days * base.getInt("base_rate") + rate.getInt("total_cost");

                    System.out.println(
                            "\nBooking details \nRoom Type:" + choice + "\nStart Date: " + start + "\nEnd Date: "
                                    + end);
                    System.out.println("Total cost: $" + total + " (" + total * 2.5 + " points)");
                    String confirm = getString(in, "\nType Y to comfirm booking or N to cancel");
                    confirm = validate(confirm, in);
                    if (confirm.toUpperCase().equals("Y")) {
                        String exist = getString(in, "\nHave you stayed with Hotel California before(Y/N)");
                        exist = validate(exist, in);

                        if (exist.toUpperCase().equals("Y")) {
                            String first = getString(in, "\nPlease enter your first name");
                            String last = getString(in, "Please enter your last name");
                            String full = first + " " + last;

                            PreparedStatement countCust = con.prepareStatement(
                                    "SELECT count(customer_id) as count from customer where name = ?");
                            countCust.setString(1, full);

                            ResultSet count = countCust.executeQuery();
                            count.next();
                            if (count.getInt("count") == 0) {
                                System.out.println(
                                        "Customer not found. Please enter following information so we can add you to our database");
                                exist = "N";
                            } else {
                                PreparedStatement cust = con.prepareStatement(
                                        "SELECT customer_id, points, credit_card, name, phone_num, street, city, state, country, zip from customer where name = ?");
                                cust.setString(1, full);
                                if (count.getInt("count") > 1) {
                                    String phone = getString(in, "Please enter your phone number (xxx-xxx-xxxx)");
                                    cust = con.prepareStatement(
                                            "SELECT customer_id, points, credit_card, name, phone_num, street, city, state, country, zip from customer where name = ? and phone_num = ?");
                                    cust.setString(1, full);
                                    cust.setString(2, phone);
                                }

                                ResultSet customer = cust.executeQuery();
                                if (customer.next()) {
                                    System.out.println("\nCustomer found\n");
                                    int custId = customer.getInt("customer_id");
                                    System.out.println("Customer Information");
                                    System.out.println("Name: " + customer.getString("name"));
                                    System.out.println("Phone Number: " + customer.getString("phone_num"));
                                    System.out.println("Address");
                                    System.out.println("Street: " + customer.getString("street"));
                                    System.out.println("City: " + customer.getString("city") + ", "
                                            + customer.getString("state") + "\n" + customer.getString("country") + ", "
                                            + customer.getString("zip"));
                                    if (customer.getInt("points") > 0) {
                                        System.out.println("Points: " + customer.getInt("points"));
                                    }

                                    int correct = getInt(in,
                                            "\n1: Confirm following information is correct\n2: Update information");
                                    while (correct != 1 && correct != 2) {
                                        correct = getInt(in, "Invalid input. Please try again");
                                    }
                                    if (correct == 2) {
                                        int pick = 0;
                                        while (pick != 4) {
                                            pick = getInt(in,
                                                    "\nSelect information to update\n1: Update name\n2: Update phone number \n3: Update address \n4: All information is updated and correct\n");
                                            if (pick == 1) {

                                                String upfirst = getString(in, "\nPlease enter your first name");
                                                while (!upfirst.matches(lregex)) {
                                                    upfirst = getString(in, "Invalid first name please try again");
                                                }
                                                String uplast = getString(in, "Please enter your last name");
                                                while (!uplast.matches(lregex)) {
                                                    uplast = getString(in, "Invalid last name please try again");
                                                }
                                                String upfull = upfirst + " " + uplast;
                                                try (PreparedStatement upName = con.prepareStatement(
                                                        "update customer set name = ? where customer_id = ?")) {
                                                    upName.setString(1, upfull);
                                                    upName.setInt(2, custId);

                                                    if (upName.executeUpdate() > 0) {
                                                        System.out.println("Name updated successfully");
                                                    }

                                                } catch (SQLException e) {
                                                    System.out.println("Unable to update name");
                                                }
                                            } else if (pick == 2) {
                                                String upphone = getString(in,
                                                        "Please enter your phone number (xxx-xxx-xxxx)");

                                                while (!upphone.matches(regex)) {
                                                    upphone = getString(in, "Invalid phone number please try again");
                                                }
                                                try (PreparedStatement upPhone = con.prepareStatement(
                                                        "update customer set phone_num = ? where customer_id = ?")) {
                                                    upPhone.setString(1, upphone);
                                                    upPhone.setInt(2, custId);

                                                    if (upPhone.executeUpdate() > 0) {
                                                        System.out.println("Phone number updated successfully");
                                                    }

                                                } catch (SQLException e) {
                                                    System.out.println("Unable to update phone number");
                                                }

                                            } else if (pick == 3) {
                                                String street = getString(in, "Please enter your street address");

                                                while (!street.matches(ldregex)) {
                                                    street = getString(in, "Invalid street address please try again");
                                                }
                                                String city = getString(in, "Please enter city");
                                                while (!city.matches(lregex)) {
                                                    city = getString(in, "Invalid city please try again");
                                                }
                                                String state = getString(in, "Please enter state");
                                                while (!state.matches(lregex)) {
                                                    state = getString(in, "Invalid state please try again");
                                                }
                                                String country = getString(in, "Please enter country");
                                                while (!country.matches(lregex)) {
                                                    country = getString(in, "Invalid country please try again");
                                                }
                                                String zip = getString(in, "Please enter zip code");
                                                regex = "\\d+";
                                                while (!zip.matches(regex)) {
                                                    zip = getString(in, "Invalid zip code please try again");
                                                }
                                                try (PreparedStatement upAdd = con.prepareStatement(
                                                        "update customer set street = ?, city = ?, state = ?, country = ?, zip = ? where customer_id = ?")) {
                                                    upAdd.setString(1, street);
                                                    upAdd.setString(2, city);
                                                    upAdd.setString(3, state);
                                                    upAdd.setString(4, country);
                                                    upAdd.setString(5, zip);
                                                    upAdd.setInt(6, custId);

                                                    if (upAdd.executeUpdate() > 0) {
                                                        System.out.println("Address updated successfully");
                                                    }

                                                } catch (SQLException e) {
                                                    System.out.println("Unable to update address");
                                                }

                                            } else if (pick == 4) {
                                                break;
                                            } else {
                                                System.out.println("Invalid input. Please try again");
                                            }
                                        }
                                    }

                                    System.out.println(
                                            "\nAdding resrvation under your name\n\nTotal cost: $"
                                                    + total
                                                    + " (" + total * 2.5 + " points)");
                                    String cnum = customer.getString("credit_card");
                                    int cust_id = customer.getInt("customer_id");
                                    customer.wasNull();
                                    boolean cred = true;
                                    boolean freq = false;
                                    if (customer.getInt("points") > 0) {
                                        freq = true;
                                        int points = customer.getInt("points");
                                        if (points > total * 2.5) {
                                            String method = getString(in, "You have " + points
                                                    + " points! Would you like to pay with points?(Y/N)");
                                            method = validate(method, in);
                                            if (method.toUpperCase().equals("Y")) {
                                                cred = false;
                                                points = (int) (points - total * 2.5);
                                                PreparedStatement upPoints = con.prepareStatement(
                                                        "update customer set points = ? where customer_id = ?");
                                                upPoints.setInt(1, points);
                                                upPoints.setInt(2, cust_id);

                                                if (upPoints.executeUpdate() > 0) {
                                                    System.out.println("Your new points balance is " + points);
                                                }

                                            }
                                        }
                                    } else {
                                        String opt = getString(in,
                                                "\nIt seems your not a part of our Frequent Guest Program.\n Frequent Guests get 50 points for every night of your stay and you can then redeem those points to pay for future reservations.\nEnter Y if you would like to join, or enter N to decline");
                                        opt = validate(opt, in);
                                        if (opt.toUpperCase().equals("Y")) {
                                            freq = true;

                                        }
                                    }
                                    if (cred) {

                                        int wcard = getInt(in,
                                                "Would you like to \n1: Pay with credit card with number: "
                                                        + cnum + " \n2: Add a new credit card\n");
                                        if (wcard == 2) {
                                            cnum = getString(in, "\nPlease enter credit card number");
                                            while (!cnum.matches(regex)) {
                                                cnum = getString(in, "Invalid card number please try again");
                                            }
                                            String cardName = getString(in, "Please enter the name on the card");
                                            while (!cardName.matches(lregex)) {
                                                cardName = getString(in, "Invalid name please try again");
                                            }
                                            String exp = getString(in,
                                                    "Please enter the expiration date on the card (Mon-YY)");
                                            while (!exp.matches(eregex)) {
                                                exp = getString(in, "Invalid expiration date please try again");
                                            }
                                            int ccv = getInt(in, "Please enter security code");

                                            try (PreparedStatement newCard = con.prepareStatement(
                                                    "INSERT INTO CreditCard (card_num, name_on_card, exp_date, ccv) VALUES (?, ?, ?, ?)")) {
                                                newCard.setString(1, cnum);
                                                newCard.setString(2, cardName);
                                                newCard.setString(3, exp);
                                                newCard.setInt(4, ccv);

                                                if (newCard.executeUpdate() > 0) {

                                                    System.out.println("Card added successfully");

                                                }

                                            } catch (SQLException e) {
                                                System.out.println(
                                                        "There was an error with adding payment method. Please try again");
                                            }

                                            try (PreparedStatement upcard = con.prepareStatement(
                                                    "update customer set credit_card = ? where customer_id = ?")) {
                                                upcard.setString(1, cnum);
                                                upcard.setInt(2, cust_id);

                                                if (upcard.executeUpdate() > 0) {
                                                    System.out.println("Card updated successfully\n");

                                                }
                                            } catch (SQLException e) {
                                                System.out.println("Error updating customer credit card");
                                            }

                                        }

                                    }

                                    try (PreparedStatement newRes = con.prepareStatement(
                                            "INSERT INTO reservation (reservation_id, hotel_id, room_type, customer, start_date, end_date, check_in_stat, cancellation, assigned_room, tot_cost) VALUES ((select max(reservation_id) from reservation) + 1,?,?,?,?,?,0,0,0,?)")) {
                                        newRes.setInt(1, hotel);
                                        newRes.setString(2, choice);
                                        newRes.setInt(3, cust_id);
                                        newRes.setString(4, start);
                                        newRes.setString(5, end);
                                        newRes.setInt(6, (int) total);

                                        if (newRes.executeUpdate() > 0) {
                                            System.out.println("Reservation added successfully\n");
                                            PreparedStatement resId = con
                                                    .prepareStatement(
                                                            "select max(reservation_id) + 1 as id from reservation ");
                                            ResultSet res = resId.executeQuery();
                                            if (res.next()) {
                                                int desId = res.getInt("id");
                                                String des = "reservation #" + desId;
                                                try (PreparedStatement trans = con.prepareStatement(
                                                        "Insert into transactions (trans_date, description, amount, credit_card) values (sysdate, ?,?,?)")) {
                                                    trans.setString(1, des);
                                                    trans.setInt(2, (int) total);
                                                    trans.setString(3, cnum);

                                                    if (trans.executeUpdate() > 0) {
                                                        System.out.println("Transaction made");
                                                    }
                                                } catch (SQLException e) {
                                                    System.out
                                                            .println("Error processing transaction. Please try again");
                                                }
                                            }

                                            if (freq) {
                                                try (PreparedStatement addpts = con.prepareStatement(
                                                        "update customer set points = ? where customer_id = ?")) {
                                                    addpts.setInt(1, days * 50);
                                                    addpts.setInt(2, cust_id);

                                                    if (addpts.executeUpdate() > 0) {
                                                        System.out
                                                                .println(days * 50
                                                                        + " points were added to your account");

                                                    }

                                                } catch (SQLException e) {
                                                    System.out.println(
                                                            "There was an error adding points. Please try again");
                                                }

                                            }

                                        }

                                    } catch (SQLException e) {
                                        System.out.println("There was an error adding reservation. Please try again");
                                    }

                                } else {
                                    System.out.println(
                                            "Customer not found. Please enter following information so we can add you to our database");
                                    exist = "N";

                                }

                            }

                        }

                        if (exist.toUpperCase().equals("N")) {

                            String first = getString(in, "\nPlease enter your first name");
                            while (!first.matches(lregex)) {
                                first = getString(in, "Invalid first name please try again");
                            }
                            String last = getString(in, "Please enter your last name");
                            while (!last.matches(lregex)) {
                                last = getString(in, "Invalid last name please try again");
                            }
                            String full = first + " " + last;
                            String phone = getString(in, "Please enter your phone number (xxx-xxx-xxxx)");

                            while (!phone.matches(regex)) {
                                phone = getString(in, "Invalid phone number please try again");
                            }
                            System.out.println("\nAddress Info");
                            String street = getString(in, "Please enter your street address");

                            while (!street.matches(ldregex)) {
                                street = getString(in, "Invalid street address please try again");
                            }
                            String city = getString(in, "Please enter city");
                            while (!city.matches(lregex)) {
                                city = getString(in, "Invalid city please try again");
                            }
                            String state = getString(in, "Please enter state");
                            while (!state.matches(lregex)) {
                                state = getString(in, "Invalid state please try again");
                            }
                            String country = getString(in, "Please enter country");
                            while (!country.matches(lregex)) {
                                country = getString(in, "Invalid country please try again");
                            }
                            String zip = getString(in, "Please enter zip code");
                            regex = "\\d+";
                            while (!zip.matches(regex)) {
                                zip = getString(in, "Invalid zip code please try again");
                            }

                            String fg = getString(in,
                                    "\nWould you like to opt into our Frequent Guests Program.\nFrequent Guests get 50 points for every night of your stay and you can then redeem those points to pay for future reservations.\nEnter Y if you would like to join, or enter N to decline");
                            fg = validate(fg, in);
                            boolean freq = false;
                            if (fg.toUpperCase().equals("Y")) {
                                freq = true;
                            }

                            System.out.println(
                                    "Adding reservation under your name");
                            System.out.println("\nPayment info");
                            System.out.println("Total cost: $" + total + " (" + total * 2.5 + " points)");
                            String cardNum = getString(in, "Please enter credit card number");
                            while (!cardNum.matches(regex)) {
                                cardNum = getString(in, "Invalid card number please try again");
                            }
                            String cardName = getString(in, "Please enter the name on the card");
                            while (!cardName.matches(lregex)) {
                                cardName = getString(in, "Invalid name please try again");
                            }
                            String exp = getString(in, "Please enter the expiration date on the card (Mon-YY)");
                            while (!exp.matches(eregex)) {
                                exp = getString(in, "Invalid expiration date please try again");
                            }
                            int ccv = getInt(in, "Please enter security code");

                            try (PreparedStatement newCard = con.prepareStatement(
                                    "INSERT INTO CreditCard (card_num, name_on_card, exp_date, ccv) VALUES (?, ?, ?, ?)")) {
                                newCard.setString(1, cardNum);
                                newCard.setString(2, cardName);
                                newCard.setString(3, exp);
                                newCard.setInt(4, ccv);

                                if (newCard.executeUpdate() > 0) {
                                    System.out.println("\nCard added successfully");

                                }

                            } catch (SQLException e) {
                                System.out.println("There was an error with payment method. Please try again");
                            }

                            int pts = 0;
                            if (freq) {
                                pts = days * 50;
                            }
                            try (PreparedStatement newCust = con.prepareStatement(
                                    "INSERT INTO Customer (customer_id, name, phone_num, credit_card, street, city, state, country, zip, points) VALUES ((select max(customer_id) from customer) + 1,?,?,?,?,?,?,?,?,?)")) {
                                newCust.setString(1, full);
                                newCust.setString(2, phone);
                                newCust.setString(3, cardNum);
                                newCust.setString(4, street);
                                newCust.setString(5, city);
                                newCust.setString(6, state);
                                newCust.setString(7, country);
                                newCust.setString(8, zip);
                                newCust.setInt(9, pts);

                                if (newCust.executeUpdate() > 0) {
                                    System.out.println("\nCustomer added successfully");

                                    PreparedStatement cust = con.prepareStatement(
                                            "SELECT customer_id from customer  where name = ? and phone_num = ?");
                                    cust.setString(1, full);
                                    cust.setString(2, phone);

                                    ResultSet customer = cust.executeQuery();
                                    customer.next();
                                    int cust_id = customer.getInt("customer_id");

                                    try (PreparedStatement newRes = con.prepareStatement(
                                            "INSERT INTO reservation (reservation_id, hotel_id, room_type, customer, start_date, end_date, check_in_stat, cancellation, assigned_room, tot_cost) VALUES ((select max(reservation_id) from reservation) + 1,?,?,?,?,?,0,0,0,?)")) {
                                        newRes.setInt(1, hotel);
                                        newRes.setString(2, choice);
                                        newRes.setInt(3, cust_id);
                                        newRes.setString(4, start);
                                        newRes.setString(5, end);
                                        newRes.setInt(6, (int) total);

                                        if (newRes.executeUpdate() > 0) {
                                            System.out.println("Reservation added successfully\n");
                                            PreparedStatement resId = con
                                                    .prepareStatement(
                                                            "select max(reservation_id) + 1 as id from reservation ");
                                            ResultSet res = resId.executeQuery();
                                            if (res.next()) {
                                                int desId = res.getInt("id");
                                                String des = "reservation #" + desId;
                                                try (PreparedStatement trans = con.prepareStatement(
                                                        "Insert into transactions (trans_date, description, amount, credit_card) values (sysdate, ?,?,?)")) {
                                                    trans.setString(1, des);
                                                    trans.setInt(2, (int) total);
                                                    trans.setString(3, cardNum);

                                                    if (trans.executeUpdate() > 0) {
                                                        System.out.println("Transaction made");
                                                    }
                                                } catch (SQLException e) {
                                                    System.out
                                                            .println("Error processing transaction. Please try again");
                                                }

                                                if (freq) {
                                                    System.out.println(pts + " points added to your account");
                                                }
                                            }

                                        }

                                    } catch (SQLException e) {
                                        System.out.println("There was an error adding reservation. Please try again");
                                    }

                                }

                            } catch (SQLException e) {
                                System.out.println("There was an error with customer info. Please try again");
                            }

                        }

                    }

                }

                else if (inter == 2) {
                    int emp_id = getInt(in, "Please your enter employee id");
                    String emp_password = getString(in, "Please enter your password");

                    PreparedStatement getEmp = con
                            .prepareStatement("select * from employees where employee_id = ? and password = ?");
                    getEmp.setInt(1, emp_id);
                    getEmp.setString(2, emp_password);

                    ResultSet login = getEmp.executeQuery();

                    if (!login.next()) {
                        System.out.println("Incorret id or password please try agian");
                    } else {
                        int hotel = login.getInt("hotel_id");
                        String staff = login.getString("department");
                        String name = login.getString("name");
                        System.out.println("\nWelcome " + name + "\nEntering hotel " + hotel + " database");

                        if (staff.equals("housekeeping")) {
                            System.out.println("Entering housekeeping interface - Veiw/update room cleaning status");
                            while (true) {
                                int room = getInt(in, "Enter room number:");

                                PreparedStatement getCleanStat = con
                                        .prepareStatement(
                                                "SELECT cleaning_stat FROM rooms WHERE hotel_id = ? AND room_num = ?");
                                getCleanStat.setInt(1, hotel);
                                getCleanStat.setInt(2, room);

                                ResultSet resultCleanStat = getCleanStat.executeQuery();
                                if (!resultCleanStat.next()) {
                                    System.out.println("This room does not exist");
                                } else {
                                    int update;

                                    if (resultCleanStat.getInt("cleaning_stat") == 1) {
                                        System.out
                                                .println(
                                                        "Room is clean and ready for check-in. Would you like to change status to 'Needs to be Cleaned'");
                                        update = 0;
                                    } else {
                                        System.out.println(
                                                "Room Needs to be Cleaned. Would you like to change status to 'Cleaned and ready for check-in'");
                                        update = 1;
                                    }

                                    String ans;
                                    ans = getString(in, "Enter 'Y' for Yes and 'N' for No");
                                    ans = validate(ans, in);

                                    if (ans.toUpperCase().equals("Y")) {
                                        PreparedStatement upCleanStat = con.prepareStatement(
                                                "UPDATE rooms SET cleaning_stat = ? WHERE hotel_id = ? AND room_num = ?");
                                        upCleanStat.setInt(1, update);
                                        upCleanStat.setInt(2, hotel);
                                        upCleanStat.setInt(3, room);

                                        ResultSet resultUpCleanStat = upCleanStat.executeQuery();
                                        if (resultUpCleanStat.next()) {
                                            System.out.println("Clenaing Status has been updated.");

                                        } else {
                                            System.out.println("Room status not changed");

                                        }
                                    }

                                    String exit = getString(in,
                                            "Would you like to view/update cleaning status for another room(Y/N)");
                                    exit = validate(exit, in);
                                    if (exit.toUpperCase().equals("N")) {
                                        break;
                                    }
                                }

                            }

                        }

                        if (staff.equals("front desk")) {
                            System.out.println("Entering front desk interface");

                            while (true) {
                                int typeCheck = getInt(in,
                                        "\nWould you like to: \n1: Check-in a customer \n2: Check-out a customer \n3: Exit");

                                if (typeCheck == 3) {
                                    break;
                                } else if (typeCheck == 1 || typeCheck == 2) {

                                    String first = getString(in, "\nPlease enter customer first name");
                                    String last = getString(in, "Please enter last last name");
                                    String full = first + " " + last;

                                    PreparedStatement countCust = con.prepareStatement(
                                            "SELECT count(customer_id) as count from customer where name = ?");
                                    countCust.setString(1, full);

                                    ResultSet count = countCust.executeQuery();
                                    count.next();
                                    if (count.getInt("count") == 0) {

                                        System.out.println("Customer not found");

                                    } else {
                                        PreparedStatement cust = con.prepareStatement(
                                                "SELECT customer_id from customer where name = ?");
                                        cust.setString(1, full);
                                        if (count.getInt("count") > 1) {
                                            String phone = getString(in,
                                                    "Please enter your phone number (xxx-xxx-xxxx)");
                                            cust = con.prepareStatement(
                                                    "SELECT customer_id from customer where name = ? and phone_num = ?");
                                            cust.setString(1, full);
                                            cust.setString(2, phone);
                                        }

                                        ResultSet customer = cust.executeQuery();

                                        if (customer.next()) {
                                            int cust_id = customer.getInt("customer_id");
                                            // find reservation
                                            PreparedStatement getRes = con.prepareStatement(
                                                    "select reservation_id, assigned_room, hotel_id, room_type, check_in_stat from reservation where customer = ?");
                                            getRes.setInt(1, cust_id);

                                            ResultSet reservation = getRes.executeQuery();
                                            if (!reservation.next()) {
                                                System.out
                                                        .println(
                                                                "\nThere is no reservation under this customer's name");
                                            } else {
                                                System.out.println("\nReservation found\nReservation ID: "
                                                        + reservation.getInt("reservation_id") + "\nRoom Type: "
                                                        + reservation.getString("room_type"));

                                                if (reservation.getInt("assigned_room") != 0) {
                                                    System.out
                                                            .println("Room Number: "
                                                                    + reservation.getInt("assigned_room"));
                                                }

                                            }
                                            if (reservation.getInt("hotel_id") != hotel) {
                                                System.out.println(
                                                        "\nCustomer has reservation at a different hotel. Cannot check-in at this location");
                                            } else if (typeCheck == 1) {
                                                if (reservation.getInt("check_in_stat") > 0) {
                                                    System.out.println("\nCustomer already checked in");
                                                } else {

                                                    PreparedStatement getRoom = con.prepareStatement(
                                                            "select room_num from rooms where hotel_id = ? and room_type = ? and occupation_stat = 0 and cleaning_stat = 1");
                                                    getRoom.setInt(1, hotel);
                                                    getRoom.setString(2, reservation.getString("room_type"));

                                                    ResultSet rooms = getRoom.executeQuery();
                                                    if (!rooms.next()) {
                                                        System.out.println("\nNo rooms available");
                                                    } else {
                                                        int room_num = rooms.getInt("room_num");
                                                        try (PreparedStatement check_in = con.prepareStatement(
                                                                "update rooms set occupation_stat = 1 where hotel_id = ? and room_num = ?");) {
                                                            check_in.setInt(1, hotel);
                                                            check_in.setInt(2, room_num);

                                                            if (check_in.executeUpdate() > 0) {
                                                                System.out
                                                                        .println(
                                                                                "\nRoom occupation status updated");
                                                            }
                                                        } catch (SQLException e) {
                                                            System.out.println(
                                                                    "\nError updating room. Please try agian");
                                                        }

                                                        try (PreparedStatement check_in_stat = con.prepareStatement(
                                                                "update reservation set check_in_stat = 1, assigned_room = ? where reservation_id = ?");) {
                                                            check_in_stat.setInt(1, room_num);
                                                            check_in_stat.setInt(2,
                                                                    reservation.getInt("reservation_id"));

                                                            if (check_in_stat.executeUpdate() > 0) {
                                                                System.out.println(
                                                                        "\nCustomer assigned to room " + room_num);
                                                                System.out.println(
                                                                        "\nCutsomer successfully checked in");
                                                            }
                                                        } catch (SQLException e) {
                                                            System.out.println(
                                                                    "\nError checking in reservation. Please try again");
                                                        }
                                                    }

                                                }

                                            } else if (typeCheck == 2) {

                                                if (reservation.getInt("check_in_stat") == 2) {
                                                    System.out.println("\nCustomer already checked out");
                                                } else if (reservation.getInt("check_in_stat") == 0) {
                                                    System.out.println("\nCustomer never checked in");
                                                } else {
                                                    int room_num = reservation.getInt("assigned_room");
                                                    try (PreparedStatement check_out = con.prepareStatement(
                                                            "update rooms set occupation_stat = 0, cleaning_stat = 0 where hotel_id = ? and room_num = ?");) {
                                                        check_out.setInt(1, hotel);
                                                        check_out.setInt(2, room_num);

                                                        if (check_out.executeUpdate() > 0) {
                                                            System.out
                                                                    .println(
                                                                            "\nRoom occupation and cleaning status updated");
                                                        }
                                                    } catch (SQLException e) {
                                                        System.out.println("\nError updating room. Please try again");
                                                    }

                                                    try (PreparedStatement check_out_stat = con.prepareStatement(
                                                            "update reservation set check_in_stat = 2, assigned_room = ? where reservation_id = ?");) {
                                                        check_out_stat.setInt(1, room_num);
                                                        check_out_stat.setInt(2, reservation.getInt("reservation_id"));

                                                        if (check_out_stat.executeUpdate() > 0) {
                                                            System.out.println("\nCutsomer successfully checked out");
                                                        }
                                                    } catch (SQLException e) {
                                                        System.out.println(
                                                                "\nError checking out reservation. Please try again");
                                                    }

                                                }

                                            }

                                        } else {
                                            System.out.println("Customer not found");
                                        }
                                    }

                                } else {
                                    System.out.println("Invalid input. Please try again");
                                }

                            }
                        }

                    }

                } else if (inter == 3) {
                    break;
                } else {
                    System.out.println("Invalid selection. Please try again");
                }

            }
            // closing out of database
            s.close();
            con.close();

        }
    }
}
