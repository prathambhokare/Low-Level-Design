# Low-Level-Design

# System Design & LLD Concepts Cheat Sheet

## Common Interview Question

### How will you prevent two users from borrowing the last available copy?

**Basic Answer**

For a single-node application, a synchronized block can prevent concurrent access.

```java
synchronized void borrowBook() {
    // borrow logic
}
```

**Better Answer**

In a distributed deployment, `synchronized` is insufficient because multiple application instances may process requests simultaneously.

Use:

* Database Transactions
* Optimistic Locking
* Pessimistic Locking

to ensure inventory consistency.

---

# 1. Resource Reservation

## Problem

Suppose a book has:

```text
Copies = 1
```

Two users request it simultaneously.

Without reservation:

```text
User A requests
↓
Waiting for approval

User B requests
↓
Waiting for approval

Admin approves both
```

Result:

```text
Copies = 1
Borrowed = 2
```

Impossible state.

## Solution

Reserve inventory immediately.

```text
Available Copies = 0
Reserved Copies = 1
```

Now no other user can request the same copy.

## Real World Examples

* BookMyShow seat booking
* Hotel reservations
* Airline seat reservations
* Amazon inventory reservation

**Key Principle:** A reservation temporarily protects a resource while a workflow is still in progress.

---

# 2. TTL (Time To Live)

## Problem

A user reserves a book but never completes payment.

The resource remains blocked forever.

## Solution

Add expiration.

```text
Reservation Time = 10:00 AM

TTL = 30 mins

Expires At = 10:30 AM
```

## State Flow

```text
AVAILABLE
    ↓
RESERVED
    ↓
BORROWED
```

or

```text
AVAILABLE
    ↓
RESERVED
    ↓
EXPIRED
    ↓
AVAILABLE
```

## Cleanup Job

```sql
SELECT *
FROM BorrowRequest
WHERE status='RESERVED'
AND expiresAt < NOW();
```

Release reserved inventory.

## Important Insight

The cleanup job is **not** the source of truth.

The source of truth is:

```java
expiresAt
```

Every operation must verify:

```java
if(now > expiresAt) {
    throw new ReservationExpiredException();
}
```

before proceeding.

---

# 3. State Machine

A state machine defines:

* Valid states
* Valid transitions

## Borrow Request States

```text
INITIATED
PENDING
APPROVED
BORROWED
RETURNED
REJECTED
EXPIRED
```

## Valid Transitions

```text
INITIATED
    ↓
PENDING
    ↓
APPROVED
    ↓
BORROWED
    ↓
RETURNED
```

## Invalid Transitions

```text
REJECTED
    ↓
BORROWED
```

```text
RETURNED
    ↓
APPROVED
```

**Why?**

State machines prevent illegal business operations and keep data consistent.

---

# 4. Transaction Boundaries

A transaction ensures:

```text
All Operations Succeed
OR
All Operations Fail
```

## Example

Borrow Book Workflow:

```text
Decrease Available Copies
Create Borrow Request
Create Payment Record
Send Notification
```

## Failure Scenario

```text
Decrease Available Copies ✔
Create Borrow Request ✔
Create Payment Record ✘
```

Now inventory has changed but payment does not exist.

## Solution

```java
@Transactional
public void borrowBook() {
    updateInventory();
    createBorrowRequest();
    createPayment();
}
```

Result:

```text
SUCCESS → Commit

Failure → Rollback
```

---

# 5. Optimistic Locking

## Idea

Assume conflicts are rare.

Detect conflicts during update.

## Entity

```java
class Book {
    Long bookId;
    int copies;

    @Version
    Long version;
}
```

## Scenario

Current state:

```text
copies = 1
version = 5
```

User A and User B both read the same row.

User A updates first:

```text
copies = 0
version = 6
```

User B's update fails because the version has changed.

## Benefits

* High performance
* No waiting
* Highly scalable

---

# 6. Pessimistic Locking

## Idea

Assume conflicts are common.

Lock the resource before modification.

## Example

```sql
SELECT *
FROM BOOK
WHERE ID = 1
FOR UPDATE;
```

## Flow

```text
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
```

until Transaction A completes.

## Benefits

* Very safe
* Prevents concurrent modifications

## Drawbacks

* Lower throughput
* Increased waiting time

---

# 7. Inventory Management

Track resource lifecycle.

## Example

```text
Total Copies = 10

Available = 7
Reserved = 2
Borrowed = 1
```

## Invariant

```text
Total =
Available +
Reserved +
Borrowed
```

## Reservation

```text
Available--
Reserved++
```

## Borrow

```text
Reserved--
Borrowed++
```

## Return

```text
Borrowed--
Available++
```

## Reservation Expiry

```text
Reserved--
Available++
```

---

# How Everything Fits Together

```text
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
```

## Concurrency Protection

* Optimistic Locking
* Pessimistic Locking

## Expired Reservations

* TTL
* Cleanup Job

## Workflow Validation

* State Machine

## Consistency

* Transactions

---

# Final Takeaway

Most real-world systems such as:

* Amazon Inventory
* Flight Booking
* Hotel Reservation
* Movie Ticket Booking
* Food Delivery
* Banking Transfers
* Library Management

use a combination of:

1. Resource Reservation
2. TTL (Expiration)
3. State Machines
4. Transactions
5. Optimistic/Pessimistic Locking
6. Inventory Management

# Advanced System Design & LLD Patterns Cheat Sheet

This document covers advanced patterns commonly discussed in SDE-2 level Low-Level Design (LLD) and System Design interviews.

---

# 8. Idempotency

## Problem

Users or services may retry the same request multiple times.

Example:

```text
Payment Service
    ↓
Payment Success
    ↓
Response Lost
    ↓
Client Retries
```

Without protection:

```text
User Charged Twice
```

---

## Solution

Use an **Idempotency Key**.

```java
class PaymentRequest {
    String requestId;
    String idempotencyKey;
}
```

Before processing:

```java
if (paymentExists(idempotencyKey)) {
    return previousResponse;
}
```

---

## Real World Examples

* Stripe
* Razorpay
* UPI
* PayPal

---

## Key Principle

The same request can be executed multiple times but should produce the same result.

---

# 9. Saga Pattern

## Problem

Distributed workflows span multiple services.

Example:

```text
Reserve Book
    ↓
Payment
    ↓
Issue Book
```

What if payment fails after reservation succeeds?

---

## Solution

Use compensating transactions.

```text
Reserve Book
    ↓
Payment Failed
    ↓
Release Reservation
```

---

## Example

```text
Flight Booking
    ↓
Hotel Booking
    ↓
Payment Failed
    ↓
Cancel Hotel
    ↓
Cancel Flight
```

---

## Key Principle

Undo completed steps when later steps fail.

---

# 10. Caching

## Problem

Repeated database queries increase latency.

Example:

```sql
SELECT *
FROM books
WHERE category='Programming';
```

---

## Solution

Store frequently accessed data in cache.

```text
Redis
In-Memory Cache
```

---

## Common Candidates for Caching

* Book Details
* Popular Books
* Categories
* Search Results

---

## Benefits

```text
Faster Response Time
Reduced Database Load
```

---

## Drawback

```text
Stale Data
```

---

# 11. Rate Limiting

## Problem

A malicious user floods the system.

```text
10000 Requests Per Minute
```

---

## Solution

Limit request rate.

Example:

```text
5 Borrow Requests / Minute / User
```

---

## Common Algorithms

### Fixed Window

```text
100 Requests Per Minute
```

### Sliding Window

More accurate request counting.

### Token Bucket

Allows controlled bursts.

### Leaky Bucket

Smoothens request flow.

---

## Key Principle

Protect system resources from abuse.

---

# 12. Retry Mechanism

## Problem

External service is temporarily unavailable.

```text
Payment Request
    ↓
Timeout
```

---

## Solution

Retry before failing.

---

## Exponential Backoff

```text
Retry 1 → 1 sec

Retry 2 → 2 sec

Retry 3 → 4 sec

Retry 4 → 8 sec
```

---

## Benefits

Handles temporary failures gracefully.

---

## Caution

Do not endlessly retry.

---

# 13. Dead Letter Queue (DLQ)

## Problem

Message processing repeatedly fails.

```text
Process Event
    ↓
Fail
    ↓
Retry
    ↓
Fail
    ↓
Retry
```

---

## Solution

Move failed messages to DLQ.

```text
Main Queue
    ↓
Fail 5 Times
    ↓
Dead Letter Queue
```

---

## Benefits

* Prevents infinite retries
* Enables manual investigation

---

## Common Usage

* Kafka
* RabbitMQ
* AWS SQS

---

# 14. Event-Driven Architecture

## Traditional Approach

```text
Borrow Service
    ↓
Call Payment Service
    ↓
Call Notification Service
    ↓
Call Audit Service
```

Tightly coupled.

---

## Event-Driven Approach

Publish:

```text
BookBorrowedEvent
```

Consumers:

```text
Payment Service
Notification Service
Audit Service
Analytics Service
```

---

## Benefits

* Loose Coupling
* Scalability
* Easier Extension

---

## Example

```text
Order Placed Event
```

Consumed by:

* Payment
* Inventory
* Notifications
* Analytics

---

# 15. Database Indexing

## Problem

Searching millions of records is slow.

Example:

```text
10 Million Books
```

Search by ISBN.

---

## Without Index

```text
Full Table Scan
```

Time Complexity:

```text
O(n)
```

---

## With Index

```sql
CREATE INDEX idx_isbn
ON books(isbn);
```

---

## Benefits

```text
Faster Searches
Lower Query Latency
```

---

## Tradeoff

Indexes increase storage and write cost.

---

# 16. Soft Delete

## Problem

Admin deletes a book.

Should history disappear forever?

---

## Hard Delete

```sql
DELETE FROM books;
```

Record permanently removed.

---

## Soft Delete

```java
class Book {
    boolean isDeleted;
}
```

---

## Query

```sql
SELECT *
FROM books
WHERE is_deleted = false;
```

---

## Benefits

* Recovery
* Auditability
* Historical Reporting

---

# 17. Audit Logging

## Purpose

Track who changed what and when.

---

## Example

```java
class AuditLog {
    Long userId;
    String action;
    LocalDateTime timestamp;
}
```

---

## Sample Records

```text
Admin Approved Request
User Returned Book
Payment Completed
```

---

## Benefits

* Compliance
* Debugging
* Security Investigation

---

# 18. Notification Pattern

## Example

```text
Book Due Tomorrow
```

```text
Reservation Expiring Soon
```

---

## Design

```text
Scheduler
    ↓
Notification Event
    ↓
Email Service
    ↓
SMS Service
```

---

## Benefits

Improves user experience and engagement.

---

# 19. CQRS (Command Query Responsibility Segregation)

## Idea

Separate reads from writes.

---

## Write Side

```text
Borrow Book
Return Book
Approve Request
```

---

## Read Side

```text
Search Books
Get Borrow History
View Reports
```

---

## Architecture

```text
Write Database
      ↓
Replication
      ↓
Read Database
```

---

## Benefits

* Better Scalability
* Read Optimization
* Independent Tuning

---

## Best For

Systems where:

```text
Reads >> Writes
```

---

# 20. Circuit Breaker

## Problem

External dependency becomes unavailable.

Example:

```text
Borrow Request
    ↓
Payment Service
    ↓
Timeout
```

Repeated calls slow down the entire application.

---

## Solution

Circuit Breaker Pattern.

---

## States

### Closed

```text
Requests Flow Normally
```

### Open

```text
Immediately Reject Requests
```

### Half-Open

```text
Allow Limited Requests
```

---

## Flow

```text
Too Many Failures
        ↓
Open Circuit
        ↓
Stop Calling Service
        ↓
Wait
        ↓
Half Open
        ↓
Recover
```

---

## Benefits

* Prevents cascading failures
* Improves resilience

---

# Summary Table

| Concept                   | Purpose                                 |
| ------------------------- | --------------------------------------- |
| Idempotency               | Prevent duplicate processing            |
| Saga Pattern              | Handle distributed transaction failures |
| Caching                   | Improve performance                     |
| Rate Limiting             | Prevent abuse                           |
| Retry Mechanism           | Handle transient failures               |
| Dead Letter Queue         | Handle permanently failed messages      |
| Event-Driven Architecture | Decouple services                       |
| Database Indexing         | Speed up queries                        |
| Soft Delete               | Preserve historical data                |
| Audit Logging             | Track changes                           |
| Notification Pattern      | Inform users of important events        |
| CQRS                      | Separate reads and writes               |
| Circuit Breaker           | Protect against dependency failures     |

---

# Final Takeaway

Most large-scale systems are built by combining these patterns:

```text
Reservation
TTL
State Machine
Transactions
Locking
Inventory Management

Idempotency
Saga
Retry
DLQ

Caching
Rate Limiting
Circuit Breaker

Event-Driven Architecture
CQRS

Indexing
Soft Delete
Audit Logging
Notifications
```

These patterns repeatedly appear in:

* E-commerce Platforms
* Banking Systems
* Payment Gateways
* Hotel Booking Systems
* Flight Reservation Systems
* Food Delivery Applications
* Library Management Systems

Mastering them provides a strong foundation for SDE-2 level LLD and System Design interviews. 🚀
