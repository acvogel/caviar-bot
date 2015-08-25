CREATE TABLE restaurant (
  id serial primary key,
  name text,
  text text, -- teaser text
  image text, -- link to restaurant image
  created_at timestamp with timezone default current_timestamp,
  updated_at timestamp with timezone default current_timestamp
);

CREATE TABLE slack_user ( -- 'user' is a reserved word
  id serial primary key,
  slack_id text,
  created_at timestamp with timezone default current_timestamp,
  updated_at timestamp with timezone default current_timestamp
);

-- caviar poll
CREATE TABLE poll (
  id serial primary key,
  created_by text, -- references slack_user(slack_id)
  default_restaurant_id int references restaurant(id),
  created_at timestamp with timezone default current_timestamp,
  updated_at timestamp with timezone default current_timestamp-- TODO figure out how to automatically update this when edited
);

-- A Caviar cart
CREATE TABLE cart (
  id serial primary key,
  restaurant_id int references restaurant(id), 
  poll_id int references poll(id),
  cart_url text,
  tracking_url text,
  created_at timestamp with timezone default current_timestamp,
  order_at timestamp with timezone -- time to order
);

-- vote for a caviar cart
CREATE TABLE vote (
  id serial primary key,
  poll_id int references poll(id),
  restaurant_id int references restaurant(id),
  created_by text, -- references slack_user(slack_id)
  created_at timestamp with timezone default current_timestamp,
  updated_at timestamp with timezone default current_timestamp -- vote can change, we don't keep around the old one
);
