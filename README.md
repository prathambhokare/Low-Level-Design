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
