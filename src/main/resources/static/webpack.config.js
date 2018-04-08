const webpack = require('webpack')
const path = require('path')
const autoprefixer = require('autoprefixer')
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: [
    'index.js'
  ],
  output: {
    filename: 'bundle.js',
    path: path.join(__dirname, "dist"),
    devtoolModuleFilenameTemplate: '[resource-path]?[hash]'
  },
  devServer: {
    contentBase: path.join(__dirname, "dist"),
    port: 3000,
    historyApiFallback: true,
    inline: true,
    proxy: {
      '**': {
        target: 'http://localhost:8080',
        secure: false,
        prependPath: false
      }
    }
  },
  resolve: {
    modules: ['src', 'node_modules'],
    extensions: ['.jsx', '.js'],
  },

  module: {
    rules: [
      {
        test: /\.(jsx|js)$/,
        loader: 'babel-loader',
        options: {
          presets: ['react', 'es2015', 'stage-0']
        },
        exclude: /node_modules/
      }, {
        test: /\.(scss|css)$/,
        use: ExtractTextPlugin.extract({
          fallback: 'style-loader',
          use: ['css-loader', 'sass-loader', 'postcss-loader']
        })
      }, {
        test: /\.(jpg|png|gif)$/,
        loader: "url-loader",
        query: {
          limit: 8196,
          name: './dist/images/[hash].[ext]'
        }
      }, {
        test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
        loader: 'url-loader',
        query: {
          limit: 4096,
          name: './dist/fonts/[hash].[ext]'
        }
      }
    ]
  },
  watchOptions: {
    aggregateTimeout: 300,
    poll: 1000,
    ignored: /node_modules/
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify(process.env.NODE_ENV)
      }
    }),
    new webpack.optimize.UglifyJsPlugin({
      output: {
        comments: false, //移除注释
      },
      compress: { //压缩脚本
        warnings: false,
        drop_console: false
      }
    }),
    new ExtractTextPlugin({
      filename: "./styles.css",
      allChunks: true
    }), //输出css
    new webpack.LoaderOptionsPlugin({
      minimize: true,
      options: {
        postcss: [autoprefixer]
      }
    }),
    new HtmlWebpackPlugin({
      title: 'untitled',
      hash: true,
      filename: './index.html',
      template: './src/index.ejs',
    })
  ],
  devtool: process.env.NODE_ENV === 'production'
    ? 'eval'
    : 'cheap-module-eval-source-map'
}