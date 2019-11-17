import React, { Component } from "react";
import Header from "./Header";
import Input from "./Input";

class GetTradeById extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tradeId: "",
      trades: {}
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleInput = this.handleInput.bind(this);
  }

  handleChange(e) {
    e.preventDefault();
    const tradeId = this.state.tradeId;
    const port = localStorage.getItem("port");
    fetch("http://localhost:" + port + "/trades/id?tradeId=" + tradeId, {
      //fetch("http://localhost:" + port + "/trades/id", {
    
    //fetch("http://localhost:" + port + "/trades/id=" + tradeId, {
      method: "GET",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      }
    })
      .then(res => {
        return res.json();
      })
      .then(data => {
        this.setState({ trades: data });
      });
  }

  renderTableData() {
    const {
      tradeId,
      fromParty,
      toParty,
      amount,
      tradeDate,
      status
    } = this.state.trades; //destructuring
    return (
      <tr key={tradeId}>
        <td>{tradeId}</td>
        <td>{fromParty}</td>
        <td>{toParty}</td>
        <td>{amount}</td>
        <td>{tradeDate}</td>
        <td>{status}</td>
      </tr>
    );
  }

  handleInput(e) {
    this.setState({ [e.target.name]: e.target.value });
  }

  render() {
    return (
      <React.Fragment>
        <Header />
        <form className="container" onSubmit={this.handleChange}>
          <Input
            type={"string"}
            title={"TradeId:"}
            name="tradeId"
            value={this.state.tradeId}
            placeholder={"Enter Trade id for search"}
            handleChange={this.handleInput}
          />
          <input type="submit" />
        </form>
        <div>
          <h1 id="title">Trades Table</h1>
          <table id="trades">
            <tbody>
              <tr>
                <th>Trade Id</th>
                <th>From Party</th>
                <th>To Party</th>
                <th>Amount</th>
                <th>Trade Date</th>
                <th>Status</th>
              </tr>
              {this.renderTableData()}
            </tbody>
          </table>
        </div>
      </React.Fragment>
    );
  }
}

export default GetTradeById;
