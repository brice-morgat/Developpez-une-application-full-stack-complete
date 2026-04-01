const path = require('path');

module.exports = {
  module: {
    rules: [
      {
        test: /\.[jt]s$/,
        include: [path.resolve(__dirname, 'src/app')],
        exclude: [/\.spec\.ts$/, /\.cy\.ts$/, /node_modules/, /cypress/],
        enforce: 'post',
        use: {
          loader: 'babel-loader',
          options: {
            presets: [],
            plugins: ['babel-plugin-istanbul'],
          },
        },
      },
    ],
  },
};
