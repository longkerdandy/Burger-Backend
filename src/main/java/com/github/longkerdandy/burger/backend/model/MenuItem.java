package com.github.longkerdandy.burger.backend.model;

import java.net.URL;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represent a menu item in restaurant's menu.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class MenuItem {

  // Menu item name
  private String name;
  // Menu item picture
  private URL image;
  // Menu item price
  private float price;
}
