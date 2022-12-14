# SpockUnitTesting
# Mitchell R. Garnatz
# University of St. Thomas Class of May 2023

This project is a java testing file created for the use of testing the CreditsController.java class within a project known as Spock. Firstly, to understand the context of the integration testing created within the file titled CreditsControllerTest.java, I will give a breif description of the entire server/project. Spock is a web project designed by my professor, Dr. Ryan Hardt, that incoporates the use of HTML, CSS, JavaScript, jQuery, AJAX, Spring, Hibernate mapping, Apache Maven, Apache Derby, JUniy to produce an efficient dynamic server. The objective of the server is to function as an enviornment adept for allowing instructors to create courses where their students can watch posted lecture videos, ask questions through comments, and reply to their peers questions and comments. 

Within this hierarchical system, SpockUser objects are associated with Course objects, and credits are associated with SpockUser objects. Within Spock's UI users are given the ability to watch, comment on, and reply to comments on lecture videos and will earn credits for their participation. CreditsControllerTest.java is a testing class that I created to test the different 
features such as getting the credits for a user, updating the credits for a user, and fufilling a purchase if a user is to make a purchase of a CourseReward object.

When run, CreditsControllerTest.java adds objects to the local database in the beforeEach() method which is apart of the JUnit testing framework. The testing methods with each be run one after the other and call the methods they are testing withing the CreditsController.java class. Many of the testing make use of the course object that is created in the beforeEach() method since it is the root for most other objects. Within Courses are Units, within Units are Lectures, and associated with all these are the Users. After referencing the database and tests have all run, the afterEach() method executes and entities that were added in the beforeEach() method have their associations removed from each other and are removed from the database.

To summarize, the CreditsControllerTest.java class executes multiple tests for the methods within the CreditsController.java class to check what features of the CreditsController are working within the project. 
