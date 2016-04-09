# Catering System

Databaseprosjekt - mer info om funskjonalitet kommer.

## TODO:
__Loginscreen is finished.__

### General:
- [ ] Check if default buttons are necessary.
- [ ] Every window needs to be handled, cant go back to parent window without closing child.
- [ ] Sorting by id's needs fixing, 90 is apparently higher than 100

### Statistics:
- [ ] Dropdown calendar for dates.
- [ ] Orders as main graph, fills upper half. (BACKEND DONE)
- [ ] Statistics as numbers in left lower corner:
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

### Users:
- [x] Remove editing in cell, add edit button which opens a window similar to add user. This window also opens on double click.
- [ ] Check out a different search-method.
- [ ] Ability to delete several users by marking them with ctrl.
- [ ] Clicking the edit button while having several users marked will open a message box saying this wont work. (FIKK IKKE TIL DETTE, LAGET TODO)
- [ ] Right click user to choose wether to edit, delete or add new user(LOW PRIORITY).

### Customers:
- [x] Remove editing in cell, add edit button which opens a window similar to add user. This window also upens on double click.
- [ ] Find users set to inactive. Let admin restore data through file dropdown.
- [ ] Check out a different search-method.
- [ ] Add single select to Customers.
- [ ] Right click user to choose wether to edit, delete or add new user(LOW PRIORITY).

### Orders:
- [ ] Change status integer to enum.
- [ ] Remove default button in add order.
- [ ] Double click will do the same as left arrow in edit order.
- [ ] Double click will make it able to edit portions in edit order.
- [ ] Double click in order list will open edit/show window.
- [x] Add clock table to orders after date table.
- [ ] Change length of tables corresponding to tekst length.
- [ ] Check out a different search-method.
- [ ] Search recipes in edit order.
- [ ] Increase comment size and decrease recipelist size in edit order.
- [ ] Recipes and portions must switch places in addOrder.

### Driver:
- [ ] Make google window-size dynamic. (CANT FIND SOLUTION)
- [x] Change list from jList to jTable. (FIXED)
- [x] Add tables to JTable: order_id, Name, phone, address.
- [ ] Fix error "UIDefaults.getUI() failed".

### Chef:
- [ ] Create button where chef can set recipe as in progress. Then update button to ready for delivery.
    Recipe will be removed is ready for delivery is pressed. A message box with ok as default will be out failsafe.
- [ ] Time recipe needs to be done should be showed in To Prepare.
- [ ] Add both current day and tomorrow in two different lists in to prepare.
- [ ] Add price to upper JLabel in add ingredients.
- [ ] Make everything in chef uneditable through cells.
- [ ] Change edit recipes to edit/show. Doubleclick will go straight to edit.
- [ ] Add edit button with double click functions for ingredients.
- [ ] Make ingredients smaller and to prepare larger.
- [ ] Fix shoppinglist! (BACKEND WORKS, FRONTEND NEEDS FIXING)
- [ ] Add possibility to store info about shopping, usage and so on (LOW PRIORITY).

### Subscriptions:
- [ ] Fix subscriptions equal to customers and orders.

### DatabaseManagement:
- [ ] Create delete user method.
- [x] Database:
    1.  Add recipe price.
    2.  Add order time.
