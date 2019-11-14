import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import Select from "./Select";
import portNum from "../PortNumber";

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      redirect: false,
      node: "",
      nodeOptions: ["Bank A", "Bank B", "Bank C"]
    };
    this.onChange = this.onChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }

  renderRedirect = () => {
    if (this.state.redirect) {
      return <Redirect to="/dashboard" />;
    }
  };

  onSubmit(e) {
    e.preventDefault();
    const node = this.state.node;
    const portN = portNum(node);
    localStorage.setItem("port", portN);
    this.setState({ redirect: true });
  }

  onChange(e) {
    this.setState({ [e.target.name]: e.target.value });
  }

  render() {
    return (
      <React.Fragment>
        <nav className="navbar navbar-expand-sm navbar-dark bg-primary mb-4">
          <div className="container">
            <a className="navbar-brand" href="/">
              Corda L3
            </a>
            <button
              className="navbar-toggler"
              type="button"
              data-toggle="collapse"
              data-target="#mobile-nav"
            >
              <span className="navbar-toggler-icon" />
            </button>
          </div>
        </nav>
        <div className="login">
          <div className="container">
            <div className="row">
              <div className="col-md-8 m-auto">
                {this.renderRedirect()}
                <h1 className="display-4 text-center">Log In</h1>
                <form onSubmit={this.onSubmit}>
                  <div className="form-group">
                    <Select
                      title={"Node:"}
                      name="node"
                      options={this.state.nodeOptions}
                      value={this.state.node}
                      placeholder={"Select Other Party"}
                      handleChange={this.onChange}
                    />
                  </div>
                  <input
                    type="submit"
                    className="btn btn-info btn-block mt-4"
                  />
                </form>
              </div>
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  }
}

export default Login;
