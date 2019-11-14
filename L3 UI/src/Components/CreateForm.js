import React, { Component } from "react";
import Header from "./Header";

/* Import Components */
import Input from "./Input";
import Select from "./Select";

class CreateForm extends Component {
  constructor(props) {
    super(props);

    this.state = {
      toParty: "",
      amount: "",
      toPartyOptions: [],
      message:""
    };
    this.handleFormSubmit = this.handleFormSubmit.bind(this);
    this.handleClearForm = this.handleClearForm.bind(this);
    this.handleInput = this.handleInput.bind(this);
  }

  /* This life cycle hook gets executed when the component mounts */

  componentDidMount() {
    const port = localStorage.getItem("port");
    fetch("http://localhost:" + port + "/peers")
      .then(res => {
        return res.json();
      })
      .then(data => {
        const party1 = data.peers[0].x500Principal.name;
        const party2 = data.peers[1].x500Principal.name;
        this.setState({ toPartyOptions: [party1, party2] });
      });
  }

  handleFormSubmit(e) {
    const port = localStorage.getItem("port");
    e.preventDefault();
    const newTrade = {
      toParty: this.state.toParty,
      amount: this.state.amount
    };
    fetch("http://localhost:" + port + "/trades/create", {
      method: "POST",
      headers: new Headers({
        "Content-Type": "application/json"
      }),
      body: JSON.stringify(newTrade)
    })
      .then(res => res.json())
      .then(data => this.setState({ message:`Trade created successfully with trade Id: ${data.tradeId}`}))
      .catch(err => console.log(err));
  }

  handleClearForm(e) {
    e.preventDefault();
    this.setState({
      toParty: "",
      amount: ""
    });
  }

  handleInput(e) {
    this.setState({ [e.target.name]: e.target.value });
  }

  render() {
    return (
      <React.Fragment>
        <Header />
        <h1>Create Trade Form</h1>
        <h3 id="output">{this.state.message}</h3>
        <br />
        <form className="container" onSubmit={this.handleFormSubmit}>
          <Select
            title={"ToParty"}
            name="toParty"
            options={this.state.toPartyOptions}
            value={this.state.toParty}
            placeholder={"Select Other Party"}
            handleChange={this.handleInput}
          />
          <Input
            type={"number"}
            title={"Amount"}
            name="amount"
            value={this.state.amount}
            placeholder={"Enter the amount"}
            handleChange={this.handleInput}
          />
          <input type="submit" />
          <input type="button" onClick={this.handleClearForm} value="Reset" />
        </form>
      </React.Fragment>
    );
  }
}

export default CreateForm;
