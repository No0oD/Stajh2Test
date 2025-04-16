// local-server.js

process.env.NODE_ENV = "development";

const { app } = require("./index");

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  //   console.log(`Firebase Function emulation: http://localhost:${PORT}/api`);

  console.log(`Local server started on port ${PORT}`);
  console.log(`API access: http://localhost:${PORT}`);
});
