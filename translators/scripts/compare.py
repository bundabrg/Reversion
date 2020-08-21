#  Reversion - Minecraft Protocol Support for Bedrock
#  Copyright (C) 2020 Reversion Developers
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.


# Compare 2 runtime_block_states and show items missing from either side
import json
import sys

left = open(sys.argv[1])
right = open(sys.argv[2])

left_json = json.load(left)
right_json = json.load(right)

print("Items in {} missing from {}".format(sys.argv[1], sys.argv[2]))

for left_item in left_json:
    found = False
    for right_item in right_json:
        if left_item == right_item:
            found = True
            break

    if not found:
        print(left_item)

print()

print("Items in {} missing from {}".format(sys.argv[2], sys.argv[1]))

for right_item in right_json:
    found = False
    for left_item in left_json:
        if right_item == left_item:
            found = True
            break

    if not found:
        print(right_item)
