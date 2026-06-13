# AI Document Analyzer

An AI-powered document analysis platform built with Spring Boot, PostgreSQL, React, and Large Language Models (Gemini/Groq).

The application allows users to upload PDF documents, extract text, generate AI-powered summaries, and track processing errors.

---

## Features Implemented

### Document Upload

* Upload PDF documents from a React frontend
* Multipart file upload API in Spring Boot
* Stores uploaded files on local disk

### Metadata Storage

Stores document metadata in PostgreSQL:

* Document ID
* Original File Name
* Stored File Name
* File Size
* Content Type
* Upload Timestamp

### Document Management

* Retrieve all uploaded documents
* Display uploaded documents in a React table
* View document details

### PDF Text Extraction

* Extract text from uploaded PDF files using Apache PDFBox
* Save extracted text into the database

### AI-Powered Summarization

* Send extracted document text to an LLM
* Generate document summaries
* Store generated summaries in PostgreSQL

### AI Error Tracking

Tracks AI processing failures:

* API rate limits
* Quota exceeded errors
* Invalid requests
* Service failures

Stores:

* AI Status
* Error Message
* Processing Result

---

## Tech Stack

### Backend

* Java 22
* Spring Boot 3
* Spring Data JPA
* PostgreSQL
* Lombok
* Apache PDFBox
* RestClient

### Frontend

* React
* Vite
* JavaScript

### Database

* PostgreSQL 16

### AI

* Google Gemini API
* Groq API (planned/alternative)

### Storage

* Local File System

---

## Architecture

User Uploads PDF

↓

React Frontend

↓

Spring Boot API

↓

Store File on Disk

↓

Save Metadata in PostgreSQL

↓

Extract PDF Text

↓

Send Text to AI Model

↓

Generate Summary

↓

Store Summary & Status

↓

Display Results in UI

---

## Database Schema

### documents

| Column             | Type      |
| ------------------ | --------- |
| id                 | UUID      |
| original_file_name | VARCHAR   |
| stored_file_name   | VARCHAR   |
| file_size          | BIGINT    |
| content_type       | VARCHAR   |
| uploaded_at        | TIMESTAMP |
| extracted_text     | TEXT      |
| ai_analysis        | TEXT      |
| ai_status          | VARCHAR   |
| ai_error           | TEXT      |

---

## API Endpoints

### Upload Document

POST

```http
/api/documents/upload
```

Request:

```multipart/form-data
file=<pdf>
```

Response:

```json
{
  "documentId": "uuid",
  "message": "File uploaded successfully"
}
```

---

### Get All Documents

GET

```http
/api/documents
```

Response:

```json
[
  {
    "id": "uuid",
    "originalFileName": "Resume.pdf",
    "contentType": "application/pdf",
    "fileSize": 76044,
    "uploadedAt": "2026-06-01T19:33:40"
  }
]
```

---

## Project Structure

```text
backend-java
├── controller
├── service
├── service/impl
├── repository
├── entity
├── dto
├── config

frontend-react
├── src
│   ├── App.jsx
│   └── components
```

---

## Current Status

Completed:

* File Upload
* Metadata Storage
* File Storage
* Document Listing
* PDF Text Extraction
* AI Summarization
* AI Error Tracking

Next Planned Features:

* Background AI Processing Queue
* AI Usage Dashboard
* Rate Limit Monitoring
* Request Statistics
* Document Search
* RAG (Retrieval-Augmented Generation)
* Chat With Documents
* User Authentication
* Docker Deployment
* Cloud Storage (AWS S3 / Azure Blob)

```
```
