## Hotel California Database Project

### Description
Hotel California is a hotel enterprise with 10 hotel locations throughout the U.S. 
This project showcases a portion of the tasks taken on by the chain’s database system. 
Through this program users can access customer and employee interfaces. 
The customer interface allows customers to see details about hotel locations, make reservations, and update their account information. 
The employee interface has two parts: a housekeeping interface and a front desk interface. 
After logging in, employees are taken to the interface corresponding to the department and hotel they work at. 
Employees at the front desk are able to check-in and check-out customers and employees in the housekeeping staff are able to update the cleaning status of rooms.



### Usage
The program will start by prompting the user to enter their database login and password. 
Once the correct login information is entered, the user will be prompted to choose to enter the customer interface,  the employee interface, or exit. 

### Customer Interface
If the user selects the customer interface, it starts by listing all of Hotel California’s locations and having the user either select a location or enter 0 to view the amenities for each location.
Once the user selects a hotel location, they will be asked to enter start and end dates for when they will be staying at the hotel. 
The program then validates the entered dates ensuring they are actual dates, that the dates occur in the future, reservations are not made more than three years in advance, that the end date occurs after the start date, and that no reservations over 30 days can be made.
Once valid dates have been entered, the program checks to see the room types available at the selected hotel for those days and lists the room type options for the user to choose from. 
Once the user selects a room type, the program will compute the total cost of the reservation in dollars and points and prompt the user to confirm or cancel the reservation. 
If the user confirms, they will be asked if they are an existing or new customer. 
If the user is a new customer they will be prompted to enter their personal and credit card information. 
Responses such as credit card number and phone number are validated to ensure they only contain numbers, however are not limited to a digit count due to potential of different credit card companies or phone numbers from other countries. 
Once information is entered this the customer, their reservation, and transaction are added to the hotel database.
If the user is an existing customer, they will be prompted to enter their name. 
If there are multiple customers with the same name, they will be asked to enter their phone number to find the correct customer. 
Once found, the customer will be asked to confirm their account information is correct, or update it if not. 
Once their information is up to date, they will be asked to select a payment method, and if they are a frequent guest member with enough points to pay for their reservation they can also choose to pay with points. 
Once all payment information is confirmed, the reservation is added to the database and a transaction is made.

#### Possible test date for existing customers
Start Date: 09-09-2024
	End Date: 09-20-2024
	Room Type: King
Customers w/ duplicate names: 
Cris Olenin	302-625-8915
	Cris Olenin	919-332-7032
Frequent guest customer w/o enough points to pay: 
	Carina Ceeley
Frequent guest w/ enough points to pay:
	Stace Burbank
Not frequent guest customer:
	Dedie Bassford
Customer not in database:
	Jonny Blue


### Employee Interface
If the user selects the employee interface, they will be asked to login using their employee id and password. 
The employee is then taken to an interface for the specific hotel and department they work at.

For employees working at the front desk, they will be asked if they want to check-in or check-out a customer. 
Once the employee selects an option, they will be asked the name of the customer they are checking in/out. 
Similar to the customer interface, customers with matching names will be differentiated through phone numbers. 
If the customer is checking in, they will be assigned a clean, unoccupied room number of the room type they reserved. 
If they are checking out the cleaning status of their assigned room will be changed to not clean. 
After a customer has been checked in/out the check-in status on their reservation will be updated.

#### Possible test data for front desk interface
	Id: 202
	Password: vPhHanu8OKm6
1. Check in
	Regular check-in/no issues:
		Karoly Podmore
Customer w/o reservation:
	Carter Greene
Customer with reservation at different hotel:
	Gisele Grewe
Customer already checked-in: 
Hymie Greensite

2. Check out
		Regular check-out/no issues:
			Hymie Greensite
Customer never checked-in:
	Taddeo Coorington
Customer already checked-out:
	Hoyt Purchall
Customers with duplicate names:
	George Savin	323-182-1294
George Savin	212-183-1020

For employees working in the housekeeping department, they will be asked to enter the room number of the room they want to check/update the cleaning status for. 
Once a valid room has been entered, the cleaning status of the room will be displayed and the employee will have the option to change the status from clean to not clean or vice versa.

#### Possible test data for housekeeping interface
	Id: 306 
	Password: 7Szj6p
	Room that is clean: 20
	Room that needs to be cleaned: 19
	Room that doesn’t exist: 75




### Database Design
Final ERD: https://drive.google.com/file/d/107RD6HN6lRBNEgYuJHxEPHsSOv44rsyK/view?usp=sharing 
Changes made to ERD
A few changes we made to database design as I developed the project. The biggest change is the addition of the employees table in order to create a system where employee interfaces can only be accessed through login information. Another change was the addition of an assigned_room attribute to reservation, in order to track the room assigned to customers after they check in. A description attribute was added to transactions in order to better track what transactions were made for.

### Notes on tables
Customer
If points = null, customer is not in frequent guest program
Rooms
If cleaning status = 1, the room is clean. If it equals 0, the room needs to be cleaned.
Reservation
If check_in_stat =  0, the customer has not checked in. If it is 1, the customer is currently checked in, if it is 2, the customer has checked out. 
If cancellation = 1, the reservation has been canceled.
If assigned_room = 0, a room has not been assigned to the reservation yet.

### Sources
Mock date generated through: https://www.mockaroo.com/
Folder with generated data files: https://drive.google.com/drive/folders/1SFO8snJKxcFAOBMx6VdVh_QpD3Y3sajJ?usp=sharing 
