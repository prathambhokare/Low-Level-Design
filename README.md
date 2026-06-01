# Low-Level-Design
System Design & LLD Concepts Cheat Sheet
Problem Statement

Consider a Library Management System where users can:

Search books
Borrow books
Return books
Reserve books
Make payments (if required)

While designing such systems, interviewers often go beyond entities and ask questions related to concurrency, consistency, transactions, and scalability.

This document covers the most important concepts required to answer those questions.

Common Interview Question
Q: How will you prevent two users from borrowing the last available copy?
Basic Answer

For a single-node application, a synchronized block can prevent concurrent access.

synchronized void borrowBook() {
   // borrow logic
}
Better Answer

In a distributed deployment, synchronized is insufficient because multiple application instances may process requests simultaneously.

Use:

Database Transactions
Optimistic Locking
Pessimistic Locking

to ensure inventory consistency.

1. Resource Reservation
Problem

Suppose a book has:

Copies = 1

Two users request it simultaneously.

Without reservation:

User A requests
↓
Waiting for approval

User B requests
↓
Waiting for approval

Admin approves both

Result:

Copies = 1
Borrowed = 2

Impossible state.

Solution

Reserve inventory immediately.

Available Copies = 0
Reserved Copies = 1

Now no other user can request the same copy.

Real World Examples
BookMyShow seat booking
Hotel reservations
Airline seat reservations
Amazon inventory reservation
Key Principle

A reservation temporarily protects a resource while a workflow is still in progress.

2. TTL (Time To Live)
Problem

A user reserves a book but never completes payment.

The resource remains blocked forever.

Solution

Add expiration.

Reservation Time = 10:00 AM

TTL = 30 mins

Expires At = 10:30 AM
State Flow
AVAILABLE
    ↓
RESERVED
    ↓
BORROWED

or

AVAILABLE
    ↓
RESERVED
    ↓
EXPIRED
    ↓
AVAILABLE
Cleanup Job
SELECT *
FROM BorrowRequest
WHERE status='RESERVED'
AND expiresAt < NOW();

Release reserved inventory.

Important Insight

The cleanup job is NOT the source of truth.

The source of truth is:

expiresAt

Every operation must verify:

if(now > expiresAt)

before proceeding.

3. State Machine

A state machine defines:

Valid states
Valid transitions
Borrow Request States
INITIATED
PENDING
APPROVED
BORROWED
RETURNED
REJECTED
EXPIRED
Valid Transitions
INITIATED
    ↓
PENDING
    ↓
APPROVED
    ↓
BORROWED
    ↓
RETURNED
Invalid Transitions
REJECTED
    ↓
BORROWED
RETURNED
    ↓
APPROVED
Why Important?

State machines prevent illegal business operations and keep data consistent.

4. Transaction Boundaries

A transaction ensures:

All Operations Succeed
OR
All Operations Fail
Example

Borrow Book Workflow:

Decrease Available Copies
Create Borrow Request
Create Payment Record
Send Notification
Failure Scenario
Decrease Available Copies ✔
Create Borrow Request ✔
Create Payment Record ✘

Now inventory has changed but payment does not exist.

Data becomes inconsistent.

Solution

Use transactions.

@Transactional
public void borrowBook() {
    updateInventory();
    createBorrowRequest();
    createPayment();
}

Result:

SUCCESS → Commit

Failure → Rollback
Banking Example

Without transaction:

Deduct ₹1000 from Account A ✔

Add ₹1000 to Account B ✘

Money disappears.

Transactions prevent such issues.

5. Optimistic Locking
Idea

Assume conflicts are rare.

Detect conflicts during update.

Entity
Book
{
    bookId
    copies
    version
}
Scenario

Current State:

copies = 1
version = 5

User A reads:

copies = 1
version = 5

User B reads:

copies = 1
version = 5
User A Updates
copies = 0
version = 6

Success.

User B Updates
UPDATE book
SET copies = 0,
    version = 6
WHERE version = 5;

Fails because:

Current version = 6
Benefits
High performance
No waiting
Highly scalable
Common Usage
@Version
private Long version;

in JPA/Hibernate.

6. Pessimistic Locking
Idea

Assume conflicts are common.

Lock the resource before modification.

Example
SELECT *
FROM BOOK
WHERE ID = 1
FOR UPDATE;
Flow
Transaction A
    ↓
LOCK BOOK
    ↓
Update
    ↓
Commit
    ↓
Unlock
Transaction B
    ↓
WAIT

until Transaction A completes.

Benefits

Very safe.

Drawbacks
Lower throughput
Increased waiting time
Reduced scalability
Optimistic vs Pessimistic Locking
Feature	Optimistic	Pessimistic
Lock Row	No	Yes
Performance	High	Lower
Wait Time	No	Yes
Conflict Handling	Detect Later	Prevent Earlier
Scalability	High	Lower
Best Use Case	Read-heavy systems	High contention systems
7. Inventory Management

Inventory management tracks the lifecycle of resources.

Example
Total Copies = 10

Track separately:

Available = 7
Reserved = 2
Borrowed = 1
Invariant

Always maintain:

Total =
Available +
Reserved +
Borrowed
Reservation
Available--
Reserved++
Borrow
Reserved--
Borrowed++
Return
Borrowed--
Available++
Reservation Expiry
Reserved--
Available++
Why Important?

Prevents:

Negative inventory
Duplicate allocations
Lost resources
Inconsistent counts
How All Concepts Work Together
User Requests Book
        ↓
Reserve Inventory
        ↓
Set TTL
        ↓
Create Borrow Request
        ↓
State = RESERVED
        ↓
Admin Approval
        ↓
State = APPROVED
        ↓
Payment
        ↓
Transaction
        ↓
State = BORROWED
Concurrency Protection

Use:

Optimistic Locking
OR
Pessimistic Locking
Expired Reservations

Handled using:

TTL
+
Cleanup Job
Workflow Validation

Handled using:

State Machine
Consistency

Handled using:

Transactions
Final Takeaway

Most real-world systems such as:

Amazon Inventory
Flight Booking
Hotel Reservation
Movie Ticket Booking
Food Delivery
Banking Transfers
Library Management

are built using a combination of:

Resource Reservation
TTL (Expiration)
State Machines
Transactions
Optimistic/Pessimistic Locking
Inventory Management

Understanding these concepts deeply allows you to design scalable, consistent, and production-ready systems and answer many SDE-2 level LLD interview questions confidently.
