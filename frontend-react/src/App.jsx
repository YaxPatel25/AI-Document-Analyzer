import { useState } from "react";

function App() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");

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
    } catch (error) {
      console.error(error);
      setMessage("Upload failed");
    }
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
        </div>
      )}
    </div>
  );
}

export default App;
