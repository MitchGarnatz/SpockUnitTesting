package spock.controllers;

import org.codesmell.jar.model.CodesmellCourse;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import spock.dao.AchievementDao;
import spock.dao.CourseUnitLectureDao;
import spock.dao.SpockUserDao;
import spock.model.*;
import spock.util.SpockUtil;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CreditsControllerTest {
    private static MockHttpServletRequest request1;
    private static MockHttpServletRequest request2;
    private static MockHttpServletRequest request3;
    private static MockHttpServletRequest request4;
    private static RedirectAttributes redirectAttributes1;
    private static RedirectAttributes redirectAttributes2;
    private static RedirectAttributes redirectAttributes3;
    private static RedirectAttributes redirectAttributes4;
    private static ModelMap model;
    private static SpockUser studentUser;
    private static SpockUser studentUserWithoutAccess;
    private static SpockUser studentUserNoCredits;
    private static SpockUser superUser;
    private static SpockUser instructorUserCourse;
    private static SpockUser instructorUserNoCourse;
    private static Course course1;
    private static CreditsController creditsController;
    private static List<CourseRewardPurchase> courseRewardPurchases;
    private static List<CourseReward> courseRewards;
    private static AchievementEarned ae1;
    private static AchievementEarned ae2;
    private static AchievementEarned ae3;
    private static AchievementEarned ae4;
    private static CourseReward courseReward;
    private static CourseRewardPurchase courseRewardPurchase;
    private static Unit unit1;
    private static Lecture lecture1;

    @BeforeAll
    public static void setup() {
        creditsController = new CreditsController();
    }

    @AfterAll
    public static void tearDown() {
    }

    @BeforeEach
    public void beforeEach() {
        model = new ModelMap();
        redirectAttributes1 = new RedirectAttributesModelMap();
        redirectAttributes2 = new RedirectAttributesModelMap();
        redirectAttributes3 = new RedirectAttributesModelMap();
        redirectAttributes4 = new RedirectAttributesModelMap();
        request1 = new MockHttpServletRequest();
        request2 = new MockHttpServletRequest();
        request3 = new MockHttpServletRequest();
        request4 = new MockHttpServletRequest();

        //create date
        Date startDate = new Date(1666827858);
        Date earlyDate = new Date(1667432658);
        Date deadlineDate = new Date(1921197689000L);

        //Creating user, course, and unit objects
        studentUser = new SpockUser("studentUser@testmail.com","salt","hash","student","last",UserType.Student);
        studentUserWithoutAccess = new SpockUser("studentUserWithoutLectureAccess@testmail.com","salt","hash","studentNoCourse","last",UserType.Guest);
        studentUserNoCredits = new SpockUser("studentUserWithoutLectureAccess@testmail.com","salt","hash","studentNoCourse","last",UserType.Student);
        instructorUserCourse = new SpockUser("instructorUserCourse@testmail.com","salt","hash","instructorCourse","last",UserType.Instructor);
        instructorUserNoCourse = new SpockUser("instructorUserNoCourse@testmail.com","salt","hash","instructorNoCourse","last",UserType.Instructor);
        superUser = new SpockUser("superUser@testmail.com","salt","hash","super","last",UserType.SuperUser);

        //initializing courses
        course1 = new Course("d1", "1", "1", CodesmellCourse.Semester.Fall, "1", "name1", AchievementDao.getAllAchievements());

        //create unit
        unit1 = new Unit(1, "unit1", startDate, earlyDate, deadlineDate);

        //create lecture
        lecture1 = new Lecture("lecture1", "url1", unit1, 1, startDate, earlyDate, deadlineDate);

        //Adding users to courses
        course1.addSpockUser(instructorUserCourse);
        course1.addSpockUser(superUser);
        course1.addSpockUser(studentUser);
        course1.addSpockUser(studentUserWithoutAccess);
        course1.addSpockUser(studentUserNoCredits);

        course1.addUnit(unit1);

        unit1.setCourse(course1);

        unit1.addLecture(lecture1);

        lecture1.setUnit(unit1);

        //Set student credits || achievementsEarned
        ae1 = new AchievementEarned(studentUser, lecture1, course1.getCourseAchievements().get(0));
        ae2 = new AchievementEarned(studentUser, lecture1, course1.getCourseAchievements().get(1));
        ae3 = new AchievementEarned(studentUser, lecture1, course1.getCourseAchievements().get(2));
        ae4 = new AchievementEarned(instructorUserCourse, lecture1, course1.getCourseAchievements().get(2));

        ae1.setNumEarned(100);
        ae2.setNumEarned(100);
        ae3.setNumEarned(100);
        ae4.setNumEarned(100);

        //Creating courseReward obj. for course1
        courseReward = new CourseReward("Drop Quiz", course1, 50);
        courseRewards = new ArrayList<>();
        courseRewards.add(courseReward);
        course1.setCourseRewards(courseRewards);

        //Creating course reward purchase
        courseRewardPurchase = new CourseRewardPurchase();
        courseRewardPurchase.setCourseReward(course1.getCourseRewards().get(0));
        courseRewardPurchase.setUser(studentUser);
        courseRewardPurchases = new ArrayList<>();
        courseRewardPurchases.add(courseRewardPurchase);

        //Adding users to DB
        SpockUserDao.addUser(studentUser);
        SpockUserDao.addUser(instructorUserCourse);
        SpockUserDao.addUser(instructorUserNoCourse);
        SpockUserDao.addUser(superUser);
        SpockUserDao.addUser(studentUserWithoutAccess);
        SpockUserDao.addUser(studentUserNoCredits);

        //Adding courses, units, lectures, and purchase to DB
        CourseUnitLectureDao.addCourse(course1);
        CourseUnitLectureDao.addUnit(unit1, course1);
        CourseUnitLectureDao.addLecture(lecture1, unit1);
        CourseUnitLectureDao.addPurchase(courseRewardPurchase);

        studentUser.setAchievementsEarned(Arrays.asList(ae1, ae2, ae3));
        instructorUserCourse.setAchievementsEarned(Arrays.asList(ae4));

        AchievementDao.addAchievementEarned(ae1);
        AchievementDao.addAchievementEarned(ae2);
        AchievementDao.addAchievementEarned(ae3);
        AchievementDao.addAchievementEarned(ae4);

        SpockUserDao.updateUser(studentUser);
        SpockUserDao.updateUser(instructorUserCourse);

        //updating users so they can see course
        studentUser = SpockUserDao.getUser(studentUser.getSpockUserId());
        instructorUserCourse = SpockUserDao.getUser(instructorUserCourse.getSpockUserId());
        instructorUserNoCourse = SpockUserDao.getUser(instructorUserNoCourse.getSpockUserId());
        superUser = SpockUserDao.getUser(superUser.getSpockUserId());
        studentUserWithoutAccess = SpockUserDao.getUser(studentUserWithoutAccess.getSpockUserId());
        studentUserNoCredits = SpockUserDao.getUser(studentUserNoCredits.getSpockUserId());

        //updating courses so they can see updated users
        course1 = CourseUnitLectureDao.getCourse(course1.getCourseId());
        unit1 = CourseUnitLectureDao.getUnit(unit1.getUnitId());
        lecture1 = CourseUnitLectureDao.getLecture(lecture1.getLectureId());

        ae1 = AchievementDao.getAchievementsEarnedForCourse(course1).get(0);
        ae2 = AchievementDao.getAchievementsEarnedForCourse(course1).get(1);
        ae3 = AchievementDao.getAchievementsEarnedForCourse(course1).get(2);
        ae4 = AchievementDao.getAchievementsEarnedForCourse(course1).get(3);

        courseRewardPurchase = CourseUnitLectureDao.getCourseRewardPurchase(courseRewardPurchase.getCourseRewardPurchaseId());
        courseReward = CourseUnitLectureDao.getCourseReward(courseReward.getCourseRewardId());
    }

    @AfterEach
    public void afterEach() {
        //remove courseReward associations
        courseRewardPurchase.setCourseReward(null);
        courseRewardPurchase.setUser(null);
        CourseUnitLectureDao.updateCourseRewardPurchase(courseRewardPurchase);

        //refresh all users
        studentUser = SpockUserDao.getUser(studentUser.getSpockUserId());
        instructorUserCourse = SpockUserDao.getUser(instructorUserCourse.getSpockUserId());
        instructorUserNoCourse = SpockUserDao.getUser(instructorUserNoCourse.getSpockUserId());
        superUser = SpockUserDao.getUser(superUser.getSpockUserId());
        studentUserWithoutAccess = SpockUserDao.getUser(studentUserWithoutAccess.getSpockUserId());
        studentUserNoCredits = SpockUserDao.getUser(studentUserNoCredits.getSpockUserId());

        //delete all users
        SpockUserDao.deleteUser(studentUser);
        SpockUserDao.deleteUser(instructorUserCourse);
        SpockUserDao.deleteUser(instructorUserNoCourse);
        SpockUserDao.deleteUser(superUser);
        SpockUserDao.deleteUser(studentUserWithoutAccess);
        SpockUserDao.deleteUser(studentUserNoCredits);

        //delete recently added entities in the database
        CourseUnitLectureDao.deleteLecture(lecture1);
        CourseUnitLectureDao.deleteUnit(unit1);
        CourseUnitLectureDao.deleteCourse(course1);

        //close request
        request1.close();
        request2.close();
        request3.close();
        request4.close();
    }

    @Test
    public void getCourseCreditsForStudentTest() {
        //user has access to get
        request1.getSession().setAttribute("user", superUser);
        String response1 = creditsController.getCourseCreditsForStudent(model, request1, redirectAttributes1, course1.getCourseId() + "");

        //user doesn't have access to get
        request2.getSession().setAttribute("user", studentUserWithoutAccess);
        String response2 = creditsController.getCourseCreditsForStudent(model, request2, redirectAttributes2, course1.getCourseId() + "");

        //user is not authenticated
        request3.getSession().setAttribute("user", null);
        String response3 = creditsController.getCourseCreditsForStudent(model, request3, redirectAttributes3, course1.getCourseId() + "");

        //an exception is thrown
        request4.getSession().setAttribute("user", studentUser);
        String response4 = creditsController.getCourseCreditsForStudent(model, request4, redirectAttributes4, "not valid id string");

        //assert
        assertEquals("credits", response1);

        assertEquals("redirect:/user/" + studentUserWithoutAccess.getSpockUserId(), response2);
        assertEquals("You do not have access to that page", redirectAttributes2.getFlashAttributes().get("error"));

        assertEquals("redirect:/login", response3);
        assertEquals("You must be logged in to access that page", redirectAttributes3.getFlashAttributes().get("error"));

        assertEquals("redirect:/user/" + studentUser.getSpockUserId(), response4);
        assertEquals("You do not have access to that page", redirectAttributes4.getFlashAttributes().get("error"));
    }

    @Test
    public void getEditStudentCreditsForInstructorTest() {
        //user has access to get
        request1.getSession().setAttribute("user", superUser);
        String response1 = creditsController.getEditStudentCreditsForInstructor(model, request1, redirectAttributes1, course1.getCourseId() + "");

        //user doesn't have access to get
        request2.getSession().setAttribute("user", studentUserWithoutAccess);
        String response2 = creditsController.getEditStudentCreditsForInstructor(model, request2, redirectAttributes2, course1.getCourseId() + "");

        //user is not authenticated
        request3.getSession().setAttribute("user", null);
        String response3 = creditsController.getEditStudentCreditsForInstructor(model, request3, redirectAttributes3, course1.getCourseId() + "");

        //an exception is thrown
        request4.getSession().setAttribute("user", studentUser);
        String response4 = creditsController.getEditStudentCreditsForInstructor(model, request4, redirectAttributes4, "not valid id string");

        //assert
        assertEquals("edit_student_credits", response1);

        assertEquals("redirect:/user/" + studentUserWithoutAccess.getSpockUserId(), response2);
        assertEquals("You do not have access to that page", redirectAttributes2.getFlashAttributes().get("error"));

        assertEquals("redirect:/login", response3);
        assertEquals("You must be logged in to access that page", redirectAttributes3.getFlashAttributes().get("error"));

        assertEquals("redirect:/user/" + studentUser.getSpockUserId(), response4);
        assertEquals("You do not have access to that page", redirectAttributes4.getFlashAttributes().get("error"));
    }

    @Test
    public void getCourseCreditsForInstructorTest() {
        //user has access to get
        request1.getSession().setAttribute("user", superUser);
        String response1 = creditsController.getCourseCreditsForInstructor(model, request1, redirectAttributes1, course1.getCourseId() + "");

        //user doesn't have access to get
        request2.getSession().setAttribute("user", studentUserWithoutAccess);
        String response2 = creditsController.getCourseCreditsForInstructor(model, request2, redirectAttributes2, course1.getCourseId() + "");

        //user is not authenticated
        request3.getSession().setAttribute("user", null);
        String response3 = creditsController.getCourseCreditsForInstructor(model, request3, redirectAttributes3, course1.getCourseId() + "");

        //an exception is thrown
        request4.getSession().setAttribute("user", studentUser);
        String response4 = creditsController.getCourseCreditsForInstructor(model, request4, redirectAttributes4, "not valid id string");

        //assert
        assertEquals("edit_credits", response1);

        assertEquals("redirect:/user/" + studentUserWithoutAccess.getSpockUserId(), response2);
        assertEquals("You do not have access to that page", redirectAttributes2.getFlashAttributes().get("error"));

        assertEquals("redirect:/login", response3);
        assertEquals("You must be logged in to access that page", redirectAttributes3.getFlashAttributes().get("error"));

        assertEquals("redirect:/user/" + studentUser.getSpockUserId(), response4);
        assertEquals("You do not have access to that page", redirectAttributes4.getFlashAttributes().get("error"));
    }

    @Test
    public void updateCourseCreditEarningTest() {
        //initializing parameters
        String courseIdStr = course1.getCourseId().toString();
        int courseAchievementId = 0;

        //user is not authenticated
        request3.getSession().setAttribute("user", null);
        String response1 = creditsController.updateCourseCreditEarning(request3, redirectAttributes3, courseIdStr);
        assertEquals("redirect:/login", response1);
        assertEquals("You must be logged in to access that page", redirectAttributes3.getFlashAttributes().get("error"));

        //user doesn't have access to update
        request2.getSession().setAttribute("user", studentUserWithoutAccess);
        String response2 = creditsController.updateCourseCreditEarning(request2, redirectAttributes2, courseIdStr);
        assertEquals("redirect:/user/" + studentUserWithoutAccess.getSpockUserId(), response2);
        assertEquals("You do not have access to that page", redirectAttributes2.getFlashAttributes().get("error"));

        //user can edit points
        request1.getSession().setAttribute("user", superUser);

        SpockUser testUser = (SpockUser)request1.getSession().getAttribute("user");

        for (int i=0; i<testUser.getCourses().get(0).getCourseAchievements().size(); i++) {
            courseAchievementId = testUser.getCourses().get(0).getCourseAchievements().get(i).getCourseAchievementId();
            request1.setParameter("" + courseAchievementId, "" + i);
        }

        String response3 = creditsController.updateCourseCreditEarning(request1, redirectAttributes1, courseIdStr);
        assertEquals("redirect:/credits/edit/" + courseIdStr, response3);
        assertEquals("Course credits updated successfully", redirectAttributes1.getFlashAttributes().get("success"));

        //exception thrown
        request4.getSession().setAttribute("user", studentUser);
        String response4 = creditsController.updateCourseCreditEarning(request4, redirectAttributes4, "not valid id string");
        assertEquals("redirect:/user/" + studentUser.getSpockUserId(), response4);
        assertEquals("You do not have access to that page", redirectAttributes4.getFlashAttributes().get("error"));
    }

    @Test
    public void updateCourseCreditSpendingTest() {
        //initializing parameters
        String courseIdStr = course1.getCourseId().toString();
        int courseRewardId = 0;

        //user is not authenticated
        request3.getSession().setAttribute("user", null);
        String response1 = creditsController.updateCourseCreditSpending(model, request3, redirectAttributes3, courseIdStr);
        assertEquals("redirect:/login", response1);
        assertEquals("You must be logged in to access that page", redirectAttributes3.getFlashAttributes().get("error"));

        //user doesn't have access to update
        request2.getSession().setAttribute("user", studentUserWithoutAccess);
        String response2 = creditsController.updateCourseCreditSpending(model, request2, redirectAttributes2, courseIdStr);
        assertEquals("redirect:/user/" + studentUserWithoutAccess.getSpockUserId(), response2);
        assertEquals("You do not have access to that page", redirectAttributes2.getFlashAttributes().get("error"));

        //user can edit points
        request1.getSession().setAttribute("user", superUser);
        //creditsController.addRewardToCourse(request1, redirectAttributes1,  courseIdStr, "Drop Quiz", "50");
        SpockUser testUser = (SpockUser)request1.getSession().getAttribute("user");
        courseRewardId = testUser.getCourses().get(0).getCourseRewards().get(0).getCourseRewardId();
        request1.setParameter("" + courseRewardId, "2");

        String response3 = creditsController.updateCourseCreditSpending(model, request1, redirectAttributes1, courseIdStr);
        assertEquals("redirect:/credits/edit/" + courseIdStr, response3);
        assertEquals("Course credits updated successfully", redirectAttributes1.getFlashAttributes().get("success"));

        //exception thrown
        request4.getSession().setAttribute("user", studentUser);
        String response4 = creditsController.updateCourseCreditSpending(model, request4, redirectAttributes4, "not valid id string");
        assertEquals("redirect:/user/" + studentUser.getSpockUserId(), response4);
        assertEquals("You do not have access to that page", redirectAttributes4.getFlashAttributes().get("error"));
    }

    @Test
    public void addRewardToCourseIsUserAuthenticated(){
        String result = "";

        MockHttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        request.getSession().setAttribute("user", null);

        //Expected: user is not authenticated
        result = creditsController.addRewardToCourse(request, redirectAttributes, course1.getCourseId().toString(), "Quiz Drop", "50");
        assertEquals("",result);

        //Expected: instructor is authenticated
        request = new MockHttpServletRequest();
        redirectAttributes = new RedirectAttributesModelMap();
        request.getSession().setAttribute("user", instructorUserCourse);
        result = creditsController.addRewardToCourse(request, redirectAttributes, course1.getCourseId().toString(), "Quiz Drop", "50");
        assertNotEquals("redirect:/user", result);
    }

    @Test
    public void addRewardToCourseEditTest() {
        String result = "";

        MockHttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        //Testing student, expect no edit permission
        request.getSession().setAttribute("user", studentUser);
        result = creditsController.addRewardToCourse(request, redirectAttributes, course1.getCourseId().toString(), "Quiz Drop", "50");
        SpockUser sessionUser = SpockUtil.getUserFromSession(request);

        assertEquals("redirect:/user/" + sessionUser.getSpockUserId(), result);
        assertEquals("You do not have access to that page", redirectAttributes.getFlashAttributes().get("error"));

        //Testing instructor, expect edit permission
        request = new MockHttpServletRequest();
        redirectAttributes = new RedirectAttributesModelMap();
        request.getSession().setAttribute("user", instructorUserCourse);
        result = creditsController.addRewardToCourse(request, redirectAttributes, course1.getCourseId().toString(), "Quiz Drop", "50");
        sessionUser = SpockUtil.getUserFromSession(request);
        assertNotEquals("redirect:/user/" + sessionUser.getSpockUserId(),result);
    }

    @Test
    public void removeRewardFromCourseEditTest() {
        String result = "";

        MockHttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        //Testing student, expect no edit permission
        request.getSession().setAttribute("user", studentUser);
        result = creditsController.removeRewardFromCourse(request, redirectAttributes, course1.getCourseId().toString(), "100000");
        SpockUser sessionUser = SpockUtil.getUserFromSession(request);

        assertEquals("redirect:/user/" + sessionUser.getSpockUserId(), result);
        assertEquals("You do not have access to that page", redirectAttributes.getFlashAttributes().get("error"));

        //Testing instructor, expect edit permission
        request = new MockHttpServletRequest();
        redirectAttributes = new RedirectAttributesModelMap();
        request.getSession().setAttribute("user", instructorUserCourse);
        result = creditsController.removeRewardFromCourse(request, redirectAttributes, course1.getCourseId().toString(), "100000");
        sessionUser = SpockUtil.getUserFromSession(request);
        assertNotEquals("redirect:/user/" + sessionUser.getSpockUserId(),result);
    }

    @Test
    public void removeRewardFromCourseIsUserAuthenticated(){
        String result = "";

        MockHttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        request.getSession().setAttribute("user", null);

        //Expected: user is not authenticated
        result = creditsController.removeRewardFromCourse(request, redirectAttributes, course1.getCourseId().toString(), "100000");
        assertEquals("",result);

        //Expected: instructor is authenticated
        request = new MockHttpServletRequest();
        redirectAttributes = new RedirectAttributesModelMap();
        request.getSession().setAttribute("user", instructorUserCourse);
        result = creditsController.removeRewardFromCourse(request, redirectAttributes, course1.getCourseId().toString(), "100000");
        assertNotEquals("redirect:/user", result);
    }

    @Test
    public void removeRewardFromCourseSuccess(){
        SpockUser testUser = new SpockUser();
        int testCourseRewardId = 0;
        String result = "";

        request1.getSession().setAttribute("user", instructorUserCourse);
        creditsController.addRewardToCourse(request1, redirectAttributes1,  course1.getCourseId().toString(), "Drop Quiz", "50");
        creditsController.addRewardToCourse(request1, redirectAttributes1,  course1.getCourseId().toString(), "Drop Assignment", "100");
        creditsController.addRewardToCourse(request1, redirectAttributes1,  course1.getCourseId().toString(), "Drop MissedLecture", "20");

        testUser = (SpockUser)request1.getSession().getAttribute("user");
        testCourseRewardId = testUser.getCourses().get(0).getCourseRewards().get(0).getCourseRewardId();

        //call method with instructorUserCourse in request, expect it to remove RewardFromCourse from course1
        result = creditsController.removeRewardFromCourse(request1, redirectAttributes1, course1.getCourseId().toString(), Integer.toString(testCourseRewardId));
        String expected;
        expected = '"' +  "success" + '"';

        //check that "success" was returned
        assertEquals(expected, result);
    }

    @Test
    public void purchaseReward() {
        String result = "";
        String rewardId = "";

        rewardId = Integer.toString(courseReward.getCourseRewardId());

        //Expected: user is not authenticated
        request1.getSession().setAttribute("user", null);
        result = creditsController.purchaseReward(request1, redirectAttributes1, rewardId);
        assertEquals("redirect:/login",result);

        //Expected: student cannot edit course
        request1 = new MockHttpServletRequest();
        redirectAttributes1 = new RedirectAttributesModelMap();
        request1.getSession().setAttribute("user", studentUserWithoutAccess);

        result = creditsController.purchaseReward(request1, redirectAttributes1, rewardId);
        assertEquals("redirect:/user/" + studentUserWithoutAccess.getSpockUserId(), result);

        //Expected: user is student and has sufficient credits to purchase
        request1 = new MockHttpServletRequest();
        redirectAttributes1 = new RedirectAttributesModelMap();
        request1.getSession().setAttribute("user", studentUser);

        result = creditsController.purchaseReward(request1, redirectAttributes1, rewardId);
        assertEquals("Your purchase was successful", redirectAttributes1.getFlashAttributes().get("success"));
        assertEquals("redirect:/credits/" + course1.getCourseId(), result);

        //Expected: User has insufficient credits to purchase
        request1 = new MockHttpServletRequest();
        redirectAttributes1 = new RedirectAttributesModelMap();
        request1.getSession().setAttribute("user", studentUserNoCredits);

        result = creditsController.purchaseReward(request1, redirectAttributes1, rewardId);
        assertEquals("You do not have enough credits for that purchase", redirectAttributes1.getFlashAttributes().get("error"));
        assertEquals("redirect:/credits/" + course1.getCourseId(), result);
    }

    @Test
    public void fulfillPurchase() {
        String result = "";
        String purchaseId = "";

        //Expected: user is not authenticated
        request1.getSession().setAttribute("user", null);
        result = creditsController.fulfillPurchase(request1, redirectAttributes1, purchaseId);
        assertEquals("redirect:/login",result);

        //Expected: student cannot edit course
        request1 = new MockHttpServletRequest();
        redirectAttributes1 = new RedirectAttributesModelMap();
        request1.getSession().setAttribute("user", studentUser);

        purchaseId = Integer.toString(courseRewardPurchase.getCourseRewardPurchaseId());
        result = creditsController.fulfillPurchase(request1, redirectAttributes1, purchaseId);
        assertEquals("redirect:/user/" + studentUser.getSpockUserId(), result);

        //Expected: instructor can edit/update fulfillment date
        request1 = new MockHttpServletRequest();
        redirectAttributes1 = new RedirectAttributesModelMap();
        request1.getSession().setAttribute("user", instructorUserCourse);

        result = creditsController.fulfillPurchase(request1, redirectAttributes1, purchaseId);
        assertEquals("redirect:/credits/edit/" + course1.getCourseId(), result);
    }
}