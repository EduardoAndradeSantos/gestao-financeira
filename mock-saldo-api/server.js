const express = require("express");
const app = express();

app.get("/saldo/:numeroConta", (req, res) => {
  const { numeroConta } = req.params;

  res.json({
    numeroConta: numeroConta,
    saldo: 2500.75
  });
});

app.listen(3001, () => {
  console.log("Mock de saldo rodando na porta 3001");
});
