import user from './user'
import contacts from './contactsManage'
import channels from './channelsManage'
import retains from './retainCustManage'
import markting from './emailMarketing'

export default [
  ...user,
  ...contacts,
  ...channels,
  ...retains,
  ...markting
]

