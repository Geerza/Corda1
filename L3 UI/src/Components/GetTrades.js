import React, { Component } from "react";
import Header from "./Header";

class GetTrades extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      trades: []
    };
  }

  componentDidMount() {
    const port = localStorage.getItem("port");
    fetch("http://localhost:" + port + "/trades")
      .then(res => {
        return res.json();
      })
      .then(data => {
        this.setState({ trades: data });
      });
  }

  renderTableData() {
    //return this.state.trades.map((trade, index) => {
    return this.state.trades.map((trade, index) => {
      const { tradeId, fromParty, toParty, amount, tradeDate, status } = trade; //destructuring
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
    });
  }

  render() {
    return (
      <React.Fragment>
        <Header />
        <h1 id="title">Trade Table</h1>
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
      </React.Fragment>
    );
  }
}

export default GetTrades;
