# Catering System

Databaseprosjekt - mer info om funskjonalitet kommer.

## TODO:
__Loginscreen is finished.__

### General:
- [x] Check if default buttons are necessary.
- [x] Every window needs to be handled, cant go back to parent window without closing child.
- [x] Sorting by id's needs fixing, 90 is apparently higher than 100


### Statistics:
    Utsetter mye så andre kan gjøre det, se på metodene mine, mange er nesten like -evda
- [x] Dropdown calendar for dates.
- [x] Orders as main graph, fills upper half. (BACKEND DONE)
- [x] Statistics as numbers in left lower corner:
    1.  Amount of orders.
    2.  Amount of cancelled orders.
    2.  Amount of new subscriptions.
    3.  Amount of cancelled subscriptions.
    4.  Amount of currently active subscriptions
    5.  Income(SAME AS OUTCOME)
    6.  Outcome (DB AND METHODS MADE, TODO: INCORPORATE IN CODE AND GUI)
    7.  Net-profit.(SAME AS OUTCOME)
- [x] Statistics in right corner: 
    1.  Amount of orders grouped by day of week.
- [ ] Modify date ranges of JDatePicker

### Users:
- [x] Remove editing in cell, add edit button which opens a window similar to add user. This window also opens on double click.
- [x] Ability to delete several users by marking them with ctrl.
- [x] Ability to delete user with delete key.
- [x] Clicking the edit button while having several users marked will open a message box saying this wont work.
- [x] Fix IndexOutOfBounds når en column er merket, så unmerket, deretter edit user blir trykket.
- [x] Right click user to choose wether to edit, delete or add new user(LOW PRIORITY).
- [x] Edit User -> Change Password: make new panel with two password-fields (new password and reenter password)
- [ ] Show deleted (inactive) users, and add possibility to reactivate them (Same as customers)

### Customers:
- [x] Remove editing in cell, add edit button which opens a window similar to add user. This window also opens on double click.
- [x] Find customers set to inactive. Let admin restore data through file dropdown.
- [x] Add single select to Customers.
- [x] Right click user to choose wether to edit, delete or add new customer(LOW PRIORITY).
- [ ] Divide customer type and ACTIVE/INACTIVE state of customer in database and create methods for setting/getting
- [ ] Test after implementing the above

### Orders:
- [x] Change status integer to enum.
- [x] Remove default button in add order.
- [x] Double click will do the same as left arrow in edit order.
- [x] Double click will make it able to edit portions in edit order.
- [x] Double click in order list will open edit/show window.
- [x] Add clock table to orders after date table.
- [x] Change length of tables corresponding to text length.(Kan ses på mer senere)
- [x] Search recipes in edit order. (Java implementation)
- [x] Increase comment size and decrease recipelist size in edit order.
- [x] Recipes and portions must switch places in addOrder.
- [ ] Change edit order calls from createOrder to updateOrder(WHEN BACKEND IS DONE).
- [x] Right click order to choose wether to edit, delete or add new order(LOW PRIORITY).
- [x] Implement JDatePicker
- [ ] Test after implementing the above

### Driver:
- [ ] Make google window-size dynamic. (CANT FIND SOLUTION)
- [x] Change list from jList to jTable. (FIXED)
- [x] Add tables to JTable: order_id, Name, phone, address.
- [ ] Fix error "UIDefaults.getUI() failed". (Probably linked to Look&Feel)
- [ ] Check error: 'SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".'
- [ ] Check out map loading on tab change
- [ ] Fix map center location (problems with asyncronous javascript)

### Chef:
- [x] Create button where chef can set recipe as in progress. Then update button to ready for delivery.
    Recipe will be removed when ready for delivery is pressed. A message box with ok as default will be our failsafe.
- [x] Time recipe needs to be done should be showed in To Prepare.
- [ ] Add both current day and tomorrow in two different lists in to prepare.
- [ ] Add price to upper JLabel in add ingredients.
- [x] Make everything in chef uneditable through cells.
- [x] Change edit recipes to edit/show. Doubleclick will go straight to edit.
- [x] Add edit button with double click functions for ingredients.
- [ ] Make ingredients smaller and to prepare larger.
- [ ] Fix shoppinglist! (BACKEND WORKS, FRONTEND NEEDS FIXING)
- [ ] Add possibility to store info about shopping, usage and so on (LOW PRIORITY).

### Subscriptions:
- [ ] Fix subscriptions equal to customers and orders.
    1.  GUI (DONE)
    2.  GUI calling methods / action listeners
    3.  Backend

### DatabaseManagement:
- [x] Create delete user method.(BRUK UPDATESTATUS)
- [ ] Create methods for updating order values
- [x] Database:
    1.  Add recipe price.
    2.  Add order time.
