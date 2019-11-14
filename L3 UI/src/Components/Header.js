import React, { Component } from "react";

class Header extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nodeName: ""
    };
  }

  componentDidMount() {
    const port = localStorage.getItem("port");
    fetch("http://localhost:" + port + "/me")
      .then(res => {
        return res.json();
      })
      .then(data => {
        this.setState({ nodeName: data.me.organisation });
      });
  }

  render() {
    return (
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

          <div className="collapse navbar-collapse" id="mobile-nav">
            <ul className="navbar-nav mr-auto">
              <li className="nav-item">
                <a className="nav-link" href="/dashboard">
                  Dashboard
                </a>
              </li>
            </ul>
          </div>

          <div className="collapse navbar-collapse" id="mobile-nav">
            <ul className="navbar-nav mr-auto">
              <li className="nav-item">
                <h5 className="nav-link">{this.state.nodeName} </h5>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    );
  }
}

export default Header;
