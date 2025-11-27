package io.github.acopier.raildest.switches

import org.apache.commons.lang3.StringUtils


/**
 * Switch type matcher, will match a type to a tag.
 */
enum class SwitchType(private val tag: String) {
  NORMAL("[destination]"), INVERTED("[!destination]");

  companion object {
    /**
     * Attempts to match a switch type to a switch tag.
     *
     * @param tag The tag to match with a type.
     * @return Returns a match switch type, or null if none are found.
     */
    fun find(tag: String?): SwitchType? {
      if (tag == null || tag.isEmpty()) {
        return null
      }
      for (type in entries) {
        if (StringUtils.equalsIgnoreCase(tag, type.tag)) {
          return type
        }
      }
      return null
    }
  }
}