import React, { Component } from 'react'
import ReactDOM from 'react-dom'
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux'
import './index.scss'
import reducers from './reducers/index'
import thunk from 'redux-thunk'
import logger from 'redux-logger'
import Main from './containers/main'

const middlewares = [];
middlewares.push(thunk)
//middlewares.push(logger)

const store = createStore(reducers, applyMiddleware(...middlewares));

class App extends Component {
    render() {
        return (
            <Provider store={store}>
                <Main/>
            </Provider>)
    }
}

ReactDOM.render(<App />, document.getElementById('root'))