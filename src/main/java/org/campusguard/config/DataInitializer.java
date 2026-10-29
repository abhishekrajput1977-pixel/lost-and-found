package org.campusguard.config;

import org.campusguard.entity.*;
import org.campusguard.entity.enums.*;
import org.campusguard.fraud.FraudEngine;
import org.campusguard.matching.SmartMatchingEngine;
import org.campusguard.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final ClaimRepository claimRepository;
    private final HandoverRecordRepository handoverRecordRepository;
    private final FraudFlagRepository fraudFlagRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmartMatchingEngine matchingEngine;
    private final FraudEngine fraudEngine;

    public DataInitializer(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           DepartmentRepository departmentRepository,
                           LocationRepository locationRepository,
                           CategoryRepository categoryRepository,
                           LostItemRepository lostItemRepository,
                           FoundItemRepository foundItemRepository,
                           ItemMatchRepository itemMatchRepository,
                           ClaimRepository claimRepository,
                           HandoverRecordRepository handoverRecordRepository,
                           FraudFlagRepository fraudFlagRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder,
                           SmartMatchingEngine matchingEngine,
                           FraudEngine fraudEngine) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.departmentRepository = departmentRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.itemMatchRepository = itemMatchRepository;
        this.claimRepository = claimRepository;
        this.handoverRecordRepository = handoverRecordRepository;
        this.fraudFlagRepository = fraudFlagRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.matchingEngine = matchingEngine;
        this.fraudEngine = fraudEngine;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data seeding.");
            return;
        }

        log.info("Starting CampusGuard Development Seed Data Initialization...");

        // 1. Seed Departments
        Department cse = departmentRepository.save(new Department("CSE", "Computer Science & Engineering"));
        Department ece = departmentRepository.save(new Department("ECE", "Electronics & Communication"));
        Department it = departmentRepository.save(new Department("IT", "Information Technology"));
        Department me = departmentRepository.save(new Department("ME", "Mechanical Engineering"));
        Department mba = departmentRepository.save(new Department("MBA", "Business Administration"));

        // 2. Seed Campus Locations
        Location lib = locationRepository.save(new Location("Central Library", "Main Building", "1st Floor quiet reading hall"));
        Location mainGate = locationRepository.save(new Location("Main Gate", "Campus Perimeter", "Security checkpoint & reception"));
        Location canteen = locationRepository.save(new Location("Student Canteen", "Food Court", "Central dining hall"));
        Location acadBlock = locationRepository.save(new Location("Academic Block A", "Block A", "Lecture Halls 101-115"));
        Location hostel = locationRepository.save(new Location("Boys Hostel 1", "Residential Zone", "Common lounge"));
        Location parking = locationRepository.save(new Location("North Parking", "Parking Lot", "Two-wheeler parking area"));
        Location lab = locationRepository.save(new Location("AI & Robotics Lab", "Technology Complex", "3rd Floor Lab 304"));
        Location aud = locationRepository.save(new Location("Main Auditorium", "Auditorium Complex", "Ground floor seating"));
        Location sports = locationRepository.save(new Location("Sports Complex", "Athletic Grounds", "Indoor Badminton Court"));
        Location studentCenter = locationRepository.save(new Location("Student Activity Center", "Student Hub", "Cafeteria area"));

        // 3. Seed Categories
        Category catElectronics = categoryRepository.save(new Category("Electronics", "laptop", "Laptops, phones, tablets, chargers"));
        Category catWallet = categoryRepository.save(new Category("Wallet", "wallet", "Wallets, purses, money clips"));
        Category catId = categoryRepository.save(new Category("ID Card", "id-card", "College ID cards, driver's licenses, badges"));
        Category catKeys = categoryRepository.save(new Category("Keys", "key", "House keys, vehicle keys, hostel room keys"));
        Category catBooks = categoryRepository.save(new Category("Books", "book", "Textbooks, notebooks, lab manuals"));
        Category catDocs = categoryRepository.save(new Category("Documents", "file-text", "Certificates, marksheets, binders"));
        Category catBags = categoryRepository.save(new Category("Bags", "briefcase", "Backpacks, duffel bags, laptop sleeves"));
        Category catClothing = categoryRepository.save(new Category("Clothing", "shirt", "Jackets, hoodies, caps, umbrellas"));
        Category catJewellery = categoryRepository.save(new Category("Jewellery", "gem", "Watches, rings, chains, bracelets"));
        Category catAccessories = categoryRepository.save(new Category("Accessories", "headphones", "Headphones, sunglasses, water bottles"));
        Category catStationery = categoryRepository.save(new Category("Stationery", "pen", "Pen drives, calculators, pencil boxes"));
        Category catOther = categoryRepository.save(new Category("Other", "box", "Miscellaneous items"));

        String commonPassword = passwordEncoder.encode("password123");

        // 4. Seed Admin User
        User adminUser = userRepository.save(new User("admin@college.test", commonPassword, "System Administrator", Role.ADMIN));
        fraudEngine.adjustTrustScore(adminUser, 100);

        // 5. Seed Security Officer User
        User securityUser = userRepository.save(new User("security@college.test", commonPassword, "Chief Security Officer", Role.SECURITY_OFFICER));
        fraudEngine.adjustTrustScore(securityUser, 100);

        // 6. Seed Staff User
        User staffUser = userRepository.save(new User("staff@college.test", commonPassword, "Prof. Rajesh Sharma", Role.STAFF));
        fraudEngine.adjustTrustScore(staffUser, 50);

        // 7. Seed 20 Student Users with Student Profiles
        List<User> students = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String email = "student" + i + "@college.test";
            String studentId = String.format("SRM2026CSE%03d", i);
            String name = getSampleName(i);

            User student = userRepository.save(new User(email, commonPassword, name, Role.STUDENT));
            Department dept = (i % 2 == 0) ? cse : (i % 3 == 0) ? ece : it;
            StudentProfile profile = new StudentProfile(student, studentId, dept, (i % 4 + 1) + "st Year", "A", "+9198765432" + String.format("%02d", i));
            studentProfileRepository.save(profile);
            student.setStudentProfile(profile);
            fraudEngine.adjustTrustScore(student, 100);
            students.add(student);
        }

        User rahul = students.get(0);
        User aman = students.get(1);
        User priya = students.get(2);
        User sneha = students.get(3);

        // 8. Seed Lost Items
        LostItem lostBackpack = new LostItem();
        lostBackpack.setItemName("Black Nike Backpack");
        lostBackpack.setCategory(catBags);
        lostBackpack.setBrand("Nike");
        lostBackpack.setColor("Black");
        lostBackpack.setDescription("Black Nike college backpack with laptop compartment and front red zipper.");
        lostBackpack.setLocation(lib);
        lostBackpack.setLostDate(LocalDate.now().minusDays(1));
        lostBackpack.setLostTime(LocalTime.of(16, 30));
        lostBackpack.setPhotoUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&q=80");
        lostBackpack.setUniqueCharacteristics("Red zipper pull tab on front pocket");
        lostBackpack.setSerialNumber("NKE-BK-9920");
        lostBackpack.setPrivateIdentifyingInfo("Small tear on left strap near shoulder cushion and blue keychain inside.");
        lostBackpack.setStatus(ItemStatus.LOST);
        lostBackpack.setReportedBy(rahul);
        LostItem savedLostBackpack = lostItemRepository.save(lostBackpack);

        LostItem lostIphone = new LostItem();
        lostIphone.setItemName("iPhone 15 Pro Blue");
        lostIphone.setCategory(catElectronics);
        lostIphone.setBrand("Apple");
        lostIphone.setColor("Blue");
        lostIphone.setDescription("Titanium Blue iPhone 15 Pro in clear silicon case.");
        lostIphone.setLocation(canteen);
        lostIphone.setLostDate(LocalDate.now().minusDays(2));
        lostIphone.setLostTime(LocalTime.of(13, 15));
        lostIphone.setPhotoUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80");
        lostIphone.setPrivateIdentifyingInfo("Lockscreen wallpaper is a golden retriever puppy and scratch on bottom right edge.");
        lostIphone.setStatus(ItemStatus.LOST);
        lostIphone.setReportedBy(priya);
        lostItemRepository.save(lostIphone);

        LostItem lostIdCard = new LostItem();
        lostIdCard.setItemName("SRM Student ID Card");
        lostIdCard.setCategory(catId);
        lostIdCard.setBrand("SRM University");
        lostIdCard.setColor("Blue/White");
        lostIdCard.setDescription("Official SRM Student ID Lanyard card belonging to Sneha Gupta.");
        lostIdCard.setLocation(acadBlock);
        lostIdCard.setLostDate(LocalDate.now().minusDays(3));
        lostIdCard.setPrivateIdentifyingInfo("Small star sticker on the back of plastic pouch.");
        lostIdCard.setStatus(ItemStatus.LOST);
        lostIdCard.setReportedBy(sneha);
        lostItemRepository.save(lostIdCard);

        for (int k = 4; k < 10; k++) {
            LostItem item = new LostItem();
            item.setItemName(getSampleLostItemName(k));
            item.setCategory(k % 2 == 0 ? catElectronics : catAccessories);
            item.setBrand(k % 2 == 0 ? "Sony" : "Casio");
            item.setColor(k % 2 == 0 ? "Black" : "Silver");
            item.setDescription("Sample lost item reported on campus for testing search filters.");
            item.setLocation(k % 2 == 0 ? parking : lab);
            item.setLostDate(LocalDate.now().minusDays(k));
            item.setPrivateIdentifyingInfo("Secret characteristic for sample lost item #" + k);
            item.setStatus(ItemStatus.LOST);
            item.setReportedBy(students.get(k));
            lostItemRepository.save(item);
        }

        // 9. Seed Found Items
        FoundItem foundBackpack = new FoundItem();
        foundBackpack.setItemName("Black Nike Backpack");
        foundBackpack.setCategory(catBags);
        foundBackpack.setBrand("Nike");
        foundBackpack.setColor("Black");
        foundBackpack.setDescription("Found black Nike backpack near 1st floor reading table in Central Library.");
        foundBackpack.setLocation(lib);
        foundBackpack.setFoundDate(LocalDate.now().minusDays(1));
        foundBackpack.setFoundTime(LocalTime.of(17, 0));
        foundBackpack.setPhotoUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&q=80");
        foundBackpack.setConditionStatus("Good Condition");
        foundBackpack.setPrivateDetails("Item handed over to library security desk. Contains notebooks and keychain.");
        foundBackpack.setStatus(ItemStatus.FOUND);
        foundBackpack.setReportedBy(aman);
        FoundItem savedFoundBackpack = foundItemRepository.save(foundBackpack);

        FoundItem foundIphone = new FoundItem();
        foundIphone.setItemName("Blue Apple iPhone");
        foundIphone.setCategory(catElectronics);
        foundIphone.setBrand("Apple");
        foundIphone.setColor("Blue");
        foundIphone.setDescription("Found smartphone on table #4 in Student Canteen.");
        foundIphone.setLocation(canteen);
        foundIphone.setFoundDate(LocalDate.now().minusDays(2));
        foundIphone.setFoundTime(LocalTime.of(13, 45));
        foundIphone.setPhotoUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80");
        foundIphone.setConditionStatus("Screen intack, battery low");
        foundIphone.setPrivateDetails("Clear case, puppy lockscreen wallpaper.");
        foundIphone.setStatus(ItemStatus.FOUND);
        foundIphone.setReportedBy(students.get(5));
        foundItemRepository.save(foundIphone);

        for (int k = 2; k < 10; k++) {
            FoundItem item = new FoundItem();
            item.setItemName(getSampleFoundItemName(k));
            item.setCategory(k % 2 == 0 ? catKeys : catBooks);
            item.setBrand(k % 2 == 0 ? "Honda" : "Pearson");
            item.setColor(k % 2 == 0 ? "Silver" : "Blue");
            item.setDescription("Found item deposited at security center for retrieval.");
            item.setLocation(k % 2 == 0 ? mainGate : aud);
            item.setFoundDate(LocalDate.now().minusDays(k));
            item.setConditionStatus("Good");
            item.setPrivateDetails("Confidential details for found item #" + k);
            item.setStatus(ItemStatus.FOUND);
            item.setReportedBy(students.get(k + 5));
            foundItemRepository.save(item);
        }

        // 10. Seed Smart Match
        ItemMatch match = matchingEngine.computeMatch(savedLostBackpack, savedFoundBackpack);
        match.setMatchScore(92.5);
        match.setConfidenceLevel(MatchConfidence.HIGH_CONFIDENCE);
        match.setStatus(MatchStatus.NOTIFIED);
        ItemMatch savedMatch = itemMatchRepository.save(match);

        savedLostBackpack.setStatus(ItemStatus.MATCHED);
        savedFoundBackpack.setStatus(ItemStatus.MATCHED);
        lostItemRepository.save(savedLostBackpack);
        foundItemRepository.save(savedFoundBackpack);

        // 11. Seed Notification
        notificationRepository.save(new Notification(
                rahul,
                "Potential Match Found for Black Nike Backpack",
                "A potential match (92.5% confidence) was found for your report at Central Library.",
                NotificationType.POTENTIAL_MATCH,
                "/items/matches/" + savedMatch.getId()
        ));

        // 12. Seed Claim & Approved Handover Record
        Claim claim = new Claim();
        claim.setLostItem(savedLostBackpack);
        claim.setFoundItem(savedFoundBackpack);
        claim.setMatch(savedMatch);
        claim.setClaimant(rahul);
        claim.setVerificationScore(95.0);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setReason("Ownership confirmed via secret mark verification and student ID validation.");
        claim.setReviewedBy(adminUser);
        claim.setReviewedAt(LocalDateTime.now());
        Claim savedClaim = claimRepository.save(claim);

        HandoverRecord handover = new HandoverRecord();
        handover.setReferenceCode("CG-HO-2026-000001");
        handover.setClaim(savedClaim);
        handover.setClaimant(rahul);
        handover.setSecurityOfficer(securityUser);
        handover.setLocation(lib);
        handover.setScheduledTime(LocalDateTime.now().plusHours(4));
        handover.setStatus(HandoverStatus.SCHEDULED);
        handover.setVerificationNotes("Ready for physical handover at Central Library security counter upon SRM ID verification.");
        handoverRecordRepository.save(handover);

        // 13. Seed Sample Fraud Flag
        FraudFlag flag = new FraudFlag();
        flag.setUser(students.get(15));
        flag.setItemType("CLAIM");
        flag.setItemId(savedClaim.getId());
        flag.setReason("Multiple rapid failed ownership verification attempts across different items.");
        flag.setSeverity(FraudSeverity.MEDIUM);
        flag.setStatus(FraudFlagStatus.PENDING_REVIEW);
        fraudFlagRepository.save(flag);

        log.info("CampusGuard Development Seed Data Initialized Successfully!");
        log.info("Demo Admin: admin@college.test | Password: password123");
        log.info("Demo Security: security@college.test | Password: password123");
        log.info("Demo Student 1: student1@college.test | Password: password123 (Student ID: SRM2026CSE001)");
    }

    private String getSampleName(int i) {
        String[] names = {
            "Rahul Sharma", "Aman Verma", "Priya Patel", "Sneha Gupta", "Vikram Singh",
            "Ananya Roy", "Rohan Mehta", "Kavya Nair", "Aditya Kumar", "Pooja Reddy",
            "Siddharth Rao", "Divya Joshi", "Arjun Das", "Ishita Kapoor", "Karan Malhotria",
            "Nisha Saxena", "Tarun Bhatia", "Meera Iyer", "Varun Chopra", "Shreya Sen"
        };
        return names[(i - 1) % names.length];
    }

    private String getSampleLostItemName(int k) {
        String[] items = {
            "Sony WH-1000XM4 Headphones", "Titan Octane Leather Watch", "Calculus & Linear Algebra Textbook",
            "Ray-Ban Aviator Sunglasses", "Casio Edifice Chronograph", "Samsung Galaxy Tab S8"
        };
        return items[(k - 4) % items.length];
    }

    private String getSampleFoundItemName(int k) {
        String[] items = {
            "Silver Key Ring with Honda Logo", "Python Data Science Handbook", "Titan Watch Silver Dial",
            "Black Sunglasses in Leather Case", "USB Flash Drive 64GB SanDisk", "Calculus Textbook 9th Edition"
        };
        return items[(k - 2) % items.length];
    }
}
