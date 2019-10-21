# finance-tracking-app

Hello, everyone! 

This is a practice app that I developed whilst learning how to code. I tried to make it as usable and as complete as I could.
I am actually using it in my day to day live to track my expenses and it does the job just fine. Hope some of you will also find it 
usefull. App consists of the following features:

  1. Add/Remove/Edit expenses as required. Each expense must belong to a certain Category and Subcategory. 
  2. See some basic statistics about your spendings.
  3. Customize your main dashboard to track your expenses and get some hints about your recemt expenses. 
  4. View a list of all expenses, with ability to filter them.
  5. All data is stored in SQLite DB on the phone, but can be synced with a Server.
  6. Enable / Disable notifications, that remind you to update your daily expenses.
  7. Setup and Track Budgets
  8. Scan QR Code on Bulgarian Receipts
  9. Export Expenses int CSV file and share them.

This project of mine really helped me learn a lot. It has a client side written in Java using the Android Framework, that makes use
of:

    - MVVM Architecture achieved through Room, ViewModel & LiveData (Part of Android Architecture Components)
    - Android Core Building Blocks such as Activities, Fragments, Intents, Services, Content Providers
    - Recycler Views and Adapters
    - Accounts and Syncing
    - Multithreading using AsyncTasks
    
It also has a simple server side written in Java using Spring Boot that allows for user Authentication and Data syncing on a remote
MySQL database. It can be seen at https://github.com/yordan-lyutskanov/finance-tracking-app-server

If you came accross my code and have any Idea how to improve it, any suggests or feedback, fell free to give them. I would highly 
appreciate any advice that will teach me something new and make me a better programmer.

Thank You :)
