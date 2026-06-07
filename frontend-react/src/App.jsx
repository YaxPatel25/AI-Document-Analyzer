import { useEffect, useState } from "react";

function App() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");
  const [documents, setDocuments] = useState([]);
  const [selectedDocument, setSelectedDocument] = useState(null);

  const uploadFile = async () => {
    if (!file) {
      alert("Select a file first");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch(
        "http://localhost:8080/api/documents/upload",
        {
          method: "POST",
          body: formData,
        },
      );

      const data = await response.json();

      setMessage(JSON.stringify(data, null, 2));

      await loadDocuments();
    } catch (error) {
      console.error(error);
      setMessage("Upload failed");
    }
  };

  const loadDocuments = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/documents");

      const data = await response.json();

      setDocuments(data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    loadDocuments();
  }, []);

  const viewDocument = async (id) => {

  const response = await fetch(
    `http://localhost:8080/api/documents/${id}`
  );

  const data = await response.json();

  setSelectedDocument(data);
  };

  return (
    <div style={{ padding: "30px" }}>
      <h1>AI Document Analyzer</h1>

      <input type="file" onChange={(e) => setFile(e.target.files[0])} />

      <br />
      <br />

      <button onClick={uploadFile}>Upload</button>

      {message && (
        <div
          style={{
            marginTop: "20px",
            padding: "10px",
            border: "1px solid #ccc",
          }}
        >
          <h3>Upload Result</h3>
          <pre>{message}</pre>
          <h2>Uploaded Documents</h2>

          <table border="1" cellPadding="10">
            <thead>
              <tr>
                <th>File Name</th>
                <th>Size</th>
                <th>Type</th>
                <th>Uploaded At</th>
                <th>View Text</th>
                <th>Download</th>
              </tr>
            </thead>

            <tbody>
              {documents.map((doc) => (
                <tr key={doc.id}>
                  <td>{doc.originalFileName}</td>
                  <td>{doc.fileSize}</td>
                  <td>{doc.contentType}</td>
                  <td>{doc.uploadedAt}</td>
                  <td>
                    <button
                      onClick={() => viewDocument(doc.id)}
                    >
                      View Text
                    </button>
                  </td>
                  <td>
                  <button
                    onClick={() =>
                      window.open(
                        `http://localhost:8080/api/documents/${doc.id}/download`,
                      )
                    }
                  >
                    Download
                  </button>
                </td>
                </tr>
              ))}
            </tbody>
          </table>
          {selectedDocument && (
          <div style={{ marginTop: "30px" }}>
            <h2>
              {selectedDocument.originalFileName}
            </h2>

            <textarea
              rows="20"
              cols="100"
              value={selectedDocument.extractedText}
              readOnly
            />
          </div>
        )}
        </div>
      )}
    </div>
  );
}

export default App;
