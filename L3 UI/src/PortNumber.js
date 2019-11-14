function portNumber(Party) {
  let port;
  switch (Party) {
    case "Bank A":
      port = "10050";
      break;
    case "Bank B":
      port = "10060";
      break;
    case "Bank C":
      port = "10070";
      break;
    default:
      port = "10050";
  }
  return port;
}

export default portNumber;
