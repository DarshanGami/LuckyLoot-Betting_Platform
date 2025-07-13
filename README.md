🚧 Work in progress!

# 🎲 LuckyLoot – Real-Time Betting Platform

LuckyLoot is a modern real-time betting platform enabling users to bet on live sports events and (coming soon) casino games. Built with a microservices architecture, LuckyLoot ensures low-latency updates, secure payment processing, and seamless user experiences.

---

## 🚀 Features

✅ **User Authentication & Authorization**  
- User registration and login  
- Email verification for account security  
- JWT-based authentication across services

✅ **Live Sports Betting**  
- View all current live sports matches  
- Real-time odds and match updates via WebSockets  
- Place bets on specific matches  
- View active bets with detailed bet slips  
- Cash out on active bets

✅ **Wallet Management**  
- Add funds using Razorpay for secure payments  
- Withdraw winnings to linked accounts  
- View wallet balance and transaction history

✅ **Casino Module (In Progress)**  
- Upcoming games and casino experiences for users  
- Real-time updates and interactive gameplay

✅ **Modern Architecture**  
- Spring Boot microservices architecture  
- API Gateway for routing, security, and service management  
- Asynchronous inter-service communication with Spring WebClient  
- MongoDB for flexible, scalable data storage

---

## 🛠️ Tech Stack

| Layer             | Technology                          |
|-------------------|-------------------------------------|
| **Frontend**      | React.js                            |
| **Backend**       | Spring Boot (Java)                  |
| **Database**      | MongoDB                             |
| **Real-Time**     | WebSocket                           |
| **Payments**      | Razorpay                            |
| **Auth**          | JWT                                  |
| **API Gateway**   | Spring Cloud Gateway                |
| **Architecture**  | Microservices                       |

---


### Prerequisites

- Node.js & npm
- Java 17+
- MongoDB
- Maven
- Razorpay Developer Account
