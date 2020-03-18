import Mock from 'mockjs'
import { param2Obj } from '../src/utils'

const List = []
const count = 100

for (let i = 0; i < count; i++) {
  List.push(Mock.mock({
    id: '@increment',
    name: '@cname(3)',
    email: '@email("gmail.com")',
    mobile: /^1[385][1-9]\d{8}/,
    organization: '@first()' + '股份有限公司',
    requirement: 666,
    channelId: '@id',
    channel: '@id',
    subChannelId: '@id',
    subChannel: '@id',
    status: '@integer(0, 2)',
    username: 'testName',
    remark: ''
  }))
}

export default [
  {
    url: '/retain/list',
    type: 'get',
    response: res => {
      const { name, page = 1, limit = 20, sort } = param2Obj(res.url)

      let mockList = List.filter(item => {
        if (name && item.name !== name) return false
        return true
      })
      if (sort === '-id') {
        mockList = mockList.reverse()
      }

      const pageList = mockList.filter((item, index) => index < limit * page && index >= limit * (page - 1))

      return {
        code: 20000,
        data: {
          total: mockList.length,
          items: pageList
        }
      }
    }
  },
  {
    url: '/retain/create',
    type: 'post',
    response: res => {
      const row = Object.assign(JSON.parse(res.body), Mock.mock({ id: '@id' }))
      List.unshift(row)
      return {
        code: 20000,
        data: 'success'
      }
    }
  },

  {
    url: '/retain/update',
    type: 'post',
    response: res => {
      const row = JSON.parse(res.body)
      for (let i = 0; i < List.length; i++) {
        if (List[i].id === row.id) {
          List.splice(i, 1, row)
          break
        }
      }
      return {
        code: 20000,
        data: 'success'
      }
    }
  }
]

