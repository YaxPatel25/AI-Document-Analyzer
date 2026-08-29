# AI Document Analyzer - Project State

_Last updated: 2026-08-23_

## Current Phase
RAG pipeline implementation (pre-pgvector) + Docker/deployment stabilization

## Completed

### Core Backend
- Spring Boot 3.5.14 project scaffolded (Java 21, Maven)
- Document upload endpoint (multipart, PDF/DOCX/TXT validation)
- Text extraction: Apache PDFBox (PDF), Apache POI (DOCX), plain read (TXT)
- `Document` entity + repository, PostgreSQL storage of metadata + extracted text
- Document listing, detail view, download, status tracking (UPLOADED/PROCESSING/COMPLETED/FAILED)

### AI Integration
- Groq API wired for summarization (`GroqConfig`, `GroqService`, `GroqRequest`/`GroqResponse` DTOs)
- Model configurable via `GROQ_MODEL` env var, default `openai/gpt-oss-120b`
- Token usage tracking (prompt/completion/total tokens stored per document)
- Input size + file size validation before calling Groq (24k char limit, 20MB file limit)
- Error tracking for AI failures (rate limits, quota, invalid requests)

### Chunking (RAG groundwork — no embeddings yet)
- `TextPreprocessor` — normalizes whitespace/line endings before chunking
- `TextChunker` — fixed-size chunking (1000 chars, 200 overlap defaults)
- `DocumentChunk` entity + repository + service — chunks created automatically on upload

### Frontend
- React 19 + Vite 8 SPA: upload, list documents, view extracted text, trigger summarization, download
- `VITE_API_URL` externalized (build-time env var)

### Infrastructure
- Docker Compose local stack: `postgres` (16), `backend`, `frontend` containers
- Backend Dockerfile: copies pre-built jar (`eclipse-temurin:21-jre`)
- Frontend Dockerfile: multi-stage Vite build → nginx
- Deployed live on Azure Container Apps (backend + frontend), Azure Postgres
- Azure DevOps pipeline drafted (`azure-pipelines.yml`): Maven build/test, npm build, Docker build+push to ACR, deploy to Container Apps

### Bugs Found & Fixed
- **Stale Docker jar**: Dockerfile only copies `target/*.jar`; any Java/config change requires `mvn clean package` before `docker compose up --build`, or the container silently runs old code
- **VITE_API_URL is build-time, not runtime**: Vite bakes this into the JS bundle at `npm run build`. Without an explicit `ARG`/`ENV` in the frontend Dockerfile + a matching `args:` block in `docker-compose.yml`, the build falls back to `.env.production` (Azure URL) or an empty string — causing the frontend to silently call the wrong backend or itself
- **YAML nesting bug**: `groq:` block was wrongly indented under `spring:` in `application.yml`, so `@Value("${groq.api-key}")` never bound to it (dead config; masked because `application-docker.yml`/`application-azure.yml` had it correctly at top level)
- **Container timezone**: `TZ` env var had a typo (`Asia/Kolkat` instead of `Asia/Kolkata`); invalid tzdata name caused the JVM to silently fall back to UTC, making `uploadedAt` timestamps look ~5.5 hours off

## Current Task
Implementing the actual RAG pipeline on top of the existing chunking layer:
1. Add `pgvector` Postgres extension + Maven/Hibernate support
2. Add an `embedding` vector column to `DocumentChunk`
3. Build an embedding service (Ollama, local)
4. Build retrieval logic (top-k cosine/vector similarity search)
5. Wire retrieved chunks into Groq generation (true RAG, replacing the current "send full text" approach)

## Technology

**Backend:** Java 21, Spring Boot 3.5.14, Maven, Spring Data JPA, Hibernate, Lombok
**Extraction:** Apache PDFBox 3.0.5, Apache POI 5.2.5
**AI (current):** Groq (generation only — no embeddings yet)
**AI (planned):** Ollama (embeddings), pgvector (vector storage)
**Database:** PostgreSQL 16 (pgvector extension not yet added)
**Frontend:** React 19, Vite 8
**Infra:** Docker Compose (local), Azure Container Apps (prod), Azure DevOps (CI/CD)

## Pending
- pgvector extension + dependency + Postgres image swap (local `docker-compose.yml` currently uses plain `postgres:16`; azure-pipelines.yml service container also needs updating once this lands)
- Embedding generation service (Ollama)
- Vector similarity retrieval logic
- Wiring RAG generation (retrieval-augmented, not full-document) into `GroqService`
- Automated backend tests beyond the default Spring context-load test
- GitHub Actions pipeline (currently only Azure DevOps exists — evaluate whether both are needed or just one)

## Important Decisions
- **Groq over Gemini**: README mentions Gemini, but Groq is what's actually implemented and wired throughout the codebase
- **RAG over naive full-text-to-LLM**: chunking + embedding + vector retrieval chosen deliberately over dumping full document text into the prompt, since it demonstrates deeper AI engineering skill and is more defensible in interviews
- **Ollama for embeddings**: local embedding model rather than a paid API, to keep the pipeline self-contained and cost-free during development
- **pgvector for storage**: keeps the whole stack in Postgres rather than introducing a separate vector DB
- **Chunk size 1000 / overlap 200**: current defaults in `TextChunker`, not yet tuned against real retrieval quality

---
*To update this file after a milestone, say: "Update the project state based on everything we completed."*